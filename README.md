# FemUtils

**FemUtils** is a utility toolkit built for Minecraft plugin developers using [Paper](https://papermc.io/), for Minecraft 1.21.1 and newer. It's designed to ease common pain points in plugin development while remaining flexible, lightweight, and expandable.

> **This project is a work in progress.** While it's usable today, more features and refinements are actively being developed.

---

## Features

- **Config Utilities** - Type-safe configuration system with comment-preserving YAML serialization.
- **Command Framework** - Fluent, argument-aware command builder with custom argument parsers and tab completion support.
- **GUI System** - Flexible GUI menus, with support for buttons, animations, paginated views, and anvil prompts.
- **General Java Utilities** - WIP, but currently includes functional result handling and time parsing from strings like `"10m"`, `"1h30m"`, etc.
- **MIT Licensed** - Use it however you like in personal or commercial projects.

---

## Installation

Currently, FemUtils is not available on a public Maven repository. To use it:

1. Clone the repository:
```bash
   git clone https://github.com/femdevv/femutils.git
````

2. Build it locally using Gradle:

```bash
   ./gradlew build
```
3. Include the built `.jar` in your plugin's `libs` or use it as a local Maven dependency.

---

## Modules

FemUtils is split into separate modules for clarity and flexibility:

| Module                        | Description                                           |
| ----------------------------- | ----------------------------------------------------- |
| `java`                        | General Java utilities                                |
| `paper`                       | Paper-specific tools like commands, GUIs, configs     |
| `compatibility-layer` *(WIP)* | Low-level NMS helper utilities (coming soon)          |
| `demo-plugin`                 | An example plugin that uses our utilities             |

More modules may be added in the future, but for now, Paper is the main focus.

---

## Roadmap / WIP Areas

We're actively expanding FemUtils. Planned areas of growth include:
* **Module System** - Lifecycle-managed modules with optional DI support to help structure larger plugins cleanly.
* **Plugin Integration Framework** - Easy and declarative way to hook into external plugins with less boilerplate.
* **NMS Utilities** - Wrappers and helpers for internal server logic.

Got ideas? Feel free to [join our discord](https://discord.gg/TVTfhXHFsz), open an issue, or contribute via pull requests!

---

## License

This project is licensed under the MIT License. See `LICENSE` for details.

---

## Contributing

We're open to feedback, improvements, or additions! Fork, star, and submit PRs, or just use it and tell us what you think!

---
