package xyz.femdev.femutils.demo.module;

import xyz.femdev.femutils.java.module.Module;
import xyz.femdev.femutils.java.module.ModuleContext;

import java.util.ArrayList;
import java.util.List;

public final class ModuleManager {

    private final ModuleContext context;
    private final List<Module> modules = new ArrayList<>();

    public ModuleManager(ModuleContext context) {
        this.context = context;
    }

    public void register(Module module) {
        modules.add(module);
    }

    public void initAll() {
        for (Module module : modules) {
            module.init(context);
        }
    }

    public void startAll() {
        for (Module module : modules) {
            module.start();
        }
    }

    public void stopAll() {
        for (Module module : modules) {
            module.stop();
        }
    }
}
