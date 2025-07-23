package xyz.femdev.femutils.demo;

import org.bukkit.plugin.java.JavaPlugin;
import xyz.femdev.femutils.demo.module.DemoModule;
import xyz.femdev.femutils.demo.module.ModuleManager;
import xyz.femdev.femutils.java.module.DefaultInjector;
import xyz.femdev.femutils.java.module.Injector;
import xyz.femdev.femutils.java.module.ModuleContext;

public final class DemoPlugin extends JavaPlugin {

    private ModuleManager moduleManager;

    @Override
    public void onEnable() {
        Injector injector = new DefaultInjector();
        ModuleContext context = new ModuleContext("demo-plugin", injector, getLogger());
        moduleManager = new ModuleManager(context);
        moduleManager.register(new DemoModule(this));
        moduleManager.initAll();
        moduleManager.startAll();
    }

    @Override
    public void onDisable() {
        if (moduleManager != null) {
            moduleManager.stopAll();
        }
    }
}
