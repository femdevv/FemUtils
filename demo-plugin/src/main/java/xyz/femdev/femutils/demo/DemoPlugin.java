package xyz.femdev.femutils.demo;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.femdev.femutils.java.config.ConfigHandle;
import xyz.femdev.femutils.paper.command.Argument;
import xyz.femdev.femutils.paper.command.BuiltinParsers;
import xyz.femdev.femutils.paper.command.CommandNode;
import xyz.femdev.femutils.paper.command.FemCommandManager;
import xyz.femdev.femutils.paper.config.PaperConfigs;
import xyz.femdev.femutils.paper.events.EventSubscription;
import xyz.femdev.femutils.paper.events.Events;
import xyz.femdev.femutils.paper.gui.*;
import xyz.femdev.femutils.paper.gui.input.AnvilPrompt;
import xyz.femdev.femutils.paper.item.ItemBuilder;
import xyz.femdev.femutils.paper.tasks.TaskChain;
import xyz.femdev.femutils.paper.tasks.Tasks;

import java.io.IOException;
import java.time.Duration;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public final class DemoPlugin extends JavaPlugin {

    private static final MiniMessage MM = MiniMessage.miniMessage();
    private final Map<UUID, EventSubscription<?>> quitSubs = new ConcurrentHashMap<>();
    private ConfigHandle<DemoConfig> configHandle;
    private FemCommandManager commands;
    private Events events;
    private Tasks tasks;
    private GuiManager guiManager;

    @Override
    public void onEnable() {
        PaperConfigs cfgs = new PaperConfigs(this);
        try {
            configHandle = cfgs.create("config.yml", DemoConfig.class, DemoConfig::defaults);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load demo config", e);
        }

        events = new Events(this);
        tasks = new Tasks(this);
        guiManager = new GuiManager(this);

        commands = new FemCommandManager(this);
        registerDemoCommands();

        getLogger().info("FemUtils demo enabled!");
    }

    public DemoConfig cfg() {
        return configHandle.get();
    }

    private void registerDemoCommands() {
        CommandNode reload = CommandNode.literal("reload")
                .permission("femutils.demo.reload")
                .exec(ctx -> {
                    try {
                        configHandle.reload();
                        ctx.msg(MM.deserialize("<green>Config reloaded."));
                    } catch (IOException e) {
                        ctx.msg(MM.deserialize("<red>Reload failed: " + e.getMessage()));
                        e.printStackTrace();
                    }
                })
                .build();

        CommandNode ping = CommandNode.literal("ping")
                .playerOnly()
                .exec(ctx -> {
                    Player p = ctx.player();
                    p.sendMessage(cfg().pingMessage());
                    var key = cfg().pingSound();
                    if (key != null) {
                        p.playSound(p.getLocation(), key.asString(), SoundCategory.MASTER, 1f, 1f);
                    }
                })
                .build();

        CommandNode tpspawn = CommandNode.literal("tpspawn")
                .playerOnly()
                .exec(ctx -> {
                    Location spawn = cfg().spawn();
                    if (spawn == null) {
                        ctx.msg(MM.deserialize("<red>No spawn set in config."));
                        return;
                    }
                    ctx.player().teleport(spawn);
                    ctx.msg(MM.deserialize("<green>Teleported to spawn."));
                })
                .build();

        CommandNode give = CommandNode.literal("give")
                .playerOnly()
                .arg(Argument.of("material", BuiltinParsers.STRING).build())
                .exec(ctx -> {
                    String matStr = ctx.get("material");
                    Material m = Material.matchMaterial(matStr);
                    if (m == null) {
                        ctx.msg(MM.deserialize("<red>Unknown material: " + matStr));
                        return;
                    }
                    if (!cfg().giveWhitelist().contains(m)) {
                        ctx.msg(MM.deserialize("<red>Material not allowed."));
                        return;
                    }
                    ctx.player().getInventory().addItem(new org.bukkit.inventory.ItemStack(m));
                    ctx.msg(MM.deserialize("<green>Gave 1 " + m.getKey().asString()));
                })
                .suggest((c, token) -> cfg().giveWhitelist().stream()
                        .map(mat -> mat.getKey().asString())
                        .filter(s -> s.startsWith(token))
                        .toList())
                .build();

        CommandNode playSound = CommandNode.literal("sound")
                .playerOnly()
                .arg(Argument.of("key", BuiltinParsers.STRING).build())
                .arg(Argument.of("volume", BuiltinParsers.DOUBLE).optional().build())
                .arg(Argument.of("pitch", BuiltinParsers.DOUBLE).optional().build())
                .exec(ctx -> {
                    Player p = ctx.player();
                    String keyStr = ctx.get("key");
                    NamespacedKey key = NamespacedKey.fromString(keyStr.contains(":") ? keyStr : "minecraft:" + keyStr);
                    double volume = (double) ctx.getOptional("volume").orElse(1.0D);
                    double pitch = (double) ctx.getOptional("pitch").orElse(1.0D);
                    p.playSound(p.getLocation(), key.asString(), SoundCategory.MASTER, (float) volume, (float) pitch);
                    ctx.msg(MM.deserialize("<yellow>Played sound: " + key.asString()));
                })
                .build();

        CommandNode waitPlace = CommandNode.literal("waitplace")
                .playerOnly()
                .arg(Argument.of("timeout", BuiltinParsers.DURATION).optional().build())
                .exec(ctx -> {
                    Player p = ctx.player();
                    Duration d = (Duration) ctx.getOptional("timeout").orElse(Duration.ofSeconds(15));
                    long ticks = d.toSeconds() * 20L;

                    ctx.msg(MM.deserialize("<gray>Place any block within " + d.getSeconds() + "s..."));

                    events.waitFor(BlockPlaceEvent.class,
                                    e -> e.getPlayer().equals(p),
                                    ticks)
                            .thenAccept(e -> p.sendMessage(MM.deserialize("<green>You placed: " + e.getBlock().getType())))
                            .exceptionally(ex -> {
                                p.sendMessage(MM.deserialize("<red>Timed out waiting for block place."));
                                return null;
                            });
                })
                .build();

        CommandNode listenQuit = CommandNode.literal("listenquit")
                .playerOnly()
                .arg(Argument.of("action", BuiltinParsers.STRING).build())
                .exec(ctx -> {
                    Player p = ctx.player();
                    String action = ((String) ctx.get("action")).toLowerCase(Locale.ROOT);
                    switch (action) {
                        case "start" -> {
                            if (quitSubs.containsKey(p.getUniqueId())) {
                                p.sendMessage(MM.deserialize("<yellow>Already listening."));
                                return;
                            }
                            var sub = events.subscribe(org.bukkit.event.player.PlayerQuitEvent.class, quit ->
                                    getLogger().info(quit.getPlayer().getName() + " left!"));
                            quitSubs.put(p.getUniqueId(), sub);
                            p.sendMessage(MM.deserialize("<green>Listening for quits (see console)."));
                        }
                        case "stop" -> {
                            var sub = quitSubs.remove(p.getUniqueId());
                            if (sub != null) {
                                sub.unsubscribe();
                                p.sendMessage(MM.deserialize("<green>Stopped listening."));
                            } else {
                                p.sendMessage(MM.deserialize("<yellow>You weren't listening."));
                            }
                        }
                        default -> p.sendMessage(MM.deserialize("<red>Use start or stop"));
                    }
                })
                .suggest((c, token) -> Stream.of("start", "stop")
                        .filter(s -> s.startsWith(token)).toList())
                .build();

        CommandNode chainDemo = CommandNode.literal("chain")
                .playerOnly()
                .exec(ctx -> {
                    Player p = ctx.player();
                    p.sendMessage(MM.deserialize("<gray>Running task chain..."));

                    TaskChain chain = tasks.chain()
                            .async(() -> {
                                Thread.sleep(200);
                                return "DB-Value";
                            })
                            .thenSync(value -> {
                                p.sendMessage(MM.deserialize("<green>Loaded: " + value));
                                return value;
                            })
                            .delayTicks(40)
                            .thenSyncRun(prev -> p.sendMessage(MM.deserialize("<yellow>40 ticks later...")))
                            .onError(t -> {
                                p.sendMessage(MM.deserialize("<red>Chain error: " + t.getMessage()));
                                t.printStackTrace();
                            })
                            .onFinally(() -> getLogger().info("Chain finished for " + p.getName()));

                    chain.execute();
                })
                .build();

        CommandNode guiCmd = CommandNode.literal("gui")
                .playerOnly()
                .exec(ctx -> {
                    Player p = ctx.player();

                    Menu menu = MenuBuilder.gui()
                            .title(MM.deserialize("<gradient:blue:green>Demo Menu</gradient>"))
                            .rows(3)
                            .button(11, Button.of(
                                    ItemBuilder.of(Material.EMERALD).name(MM.deserialize("<green>Click me")).build(),
                                    click -> click.player().sendMessage(MM.deserialize("<yellow>You clicked the emerald!"))
                            ))
                            .button(15, Button.of(
                                    ItemBuilder.of(Material.BARRIER).name(MM.deserialize("<red>Close")).build(),
                                    click -> click.player().closeInventory()
                            ))
                            .onClose(player -> player.sendMessage(MM.deserialize("<gray>Menu closed.")))
                            .build();

                    guiManager.open(p, menu);
                })
                .build();

        CommandNode paginatorCmd = CommandNode.literal("paginator")
                .playerOnly()
                .exec(ctx -> {
                    Player p = ctx.player();

                    java.util.List<Material> mats = java.util.Arrays.stream(Material.values())
                            .filter(Material::isItem)
                            .toList();

                    LayoutMask mask = LayoutMask.of(
                            "#########",
                            "#.......#",
                            "#.......#",
                            "#########"
                    );

                    int[] grid = mask.slots('.');

                    PaginatedMenu<Material> paginated = new PaginatedMenu<>(
                            MM.deserialize("<gold>Materials"), 4, mats, grid,
                            m -> Button.of(
                                    new org.bukkit.inventory.ItemStack(m),
                                    click -> click.player().sendMessage(MM.deserialize("<green>Clicked " + m.name()))
                            )
                    );

                    Button prev = Button.of(ItemBuilder.of(Material.ARROW).name(MM.deserialize("<yellow>Prev")).build(), ctx2 -> {
                    });
                    Button next = Button.of(ItemBuilder.of(Material.ARROW).name(MM.deserialize("<yellow>Next")).build(), ctx2 -> {
                    });
                    paginated.navigation(27, prev, 35, next);

                    guiManager.open(p, paginated);
                })
                .build();

        CommandNode animCmd = CommandNode.literal("anim")
                .playerOnly()
                .exec(ctx -> {
                    Player p = ctx.player();

                    var f1 = ItemBuilder.of(Material.SLIME_BALL).name(MM.deserialize("<green>Bouncy")).build();
                    var f2 = ItemBuilder.of(Material.MAGMA_CREAM).name(MM.deserialize("<red>Hot!")).build();
                    AnimatedItem anim = new AnimatedItem(java.util.List.of(f1, f2), 10);

                    Menu menu = MenuBuilder.gui()
                            .title(MM.deserialize("<gradient:red:gold>Animated</gradient>"))
                            .rows(3)
                            .animatedButton(13, anim, clickContext -> clickContext.player().sendMessage(MM.deserialize("<yellow>Clicked!")))
                            .build();

                    guiManager.open(p, menu);
                })
                .build();

        CommandNode anvilCmd = CommandNode.literal("anvil")
                .playerOnly()
                .exec(ctx -> {
                    Player p = ctx.player();
                    AnvilPrompt prompt = new AnvilPrompt(this);
                    prompt.open(
                            p,
                            MM.deserialize("<blue>Enter Text"),
                            "Type here",
                            text -> p.sendMessage(MM.deserialize("<green>You typed: " + text)),
                            () -> p.sendMessage(MM.deserialize("<red>Cancelled"))
                    );
                })
                .build();

        CommandNode root = CommandNode.literal("demo")
                .child(reload)
                .child(ping)
                .child(tpspawn)
                .child(give)
                .child(playSound)
                .child(waitPlace)
                .child(listenQuit)
                .child(chainDemo)
                .child(guiCmd)
                .child(paginatorCmd)
                .child(animCmd)
                .child(anvilCmd)
                .build();

        commands.registerRoot("demo", root);
    }
}
