# Veinminer Crouch

**Veinminer Crouch** is a lightweight, server-friendly Minecraft Fabric utility mod that mines entire connected ore veins when sneaking (crouching) with the appropriate tool.

---

## ✨ Features

- **Sneak-to-Activate**: Mine an entire ore vein simply by crouching while breaking an ore block.
- **Strictly Ores**: Only targets matching connected ore blocks (vanilla and modded ores registered under standard ore tags).
- **Anti-Lag Drop Bundling**: Automatically merges and spawns ore drops together at the initial broken block position to eliminate entity/tick lag.
- **Configurable**: Configurable max ore limit (default: 64) and tool durability wear settings.
- **Server & Singleplayer Ready**: Works cleanly on dedicated servers (vanilla clients can join and use it) as well as singleplayer clients.
- **In-Game GUI**: Includes a ModMenu-compatible config screen.

---

## 📥 Downloadable Releases

Pre-compiled JARs for all supported Minecraft versions are available in the [`outputs/`](./outputs) directory or on [Modrinth](https://modrinth.com/mod/veinminer-crouch):

| Minecraft Versions | Release JAR |
| :--- | :--- |
| **Minecraft 26.3** | [`VeinMiner-Fabric-26.3-1.0.0+mc26.3.jar`](./outputs/VeinMiner-Fabric-26.3-1.0.0+mc26.3.jar) |
| **Minecraft 26.2** | [`VeinMiner-Fabric-26.2-1.0.0+mc26.2.jar`](./outputs/VeinMiner-Fabric-26.2-1.0.0+mc26.2.jar) |
| **Minecraft 1.21.11** | [`VeinMiner-Fabric-1.21.11-1.0.0.jar`](./outputs/VeinMiner-Fabric-1.21.11-1.0.0.jar) |
| **Minecraft 1.21.1 – 1.21.10** | [`CrouchVeinMiner-Fabric-1.21.x-1.0.0.jar`](./outputs) |
| **Minecraft 1.20 – 1.20.6** | [`CrouchVeinMiner-Fabric-1.20.x-1.0.0.jar`](./outputs) |

---

## 🛠️ Building From Source

Run the Gradle wrapper:

```bash
# Build for Minecraft 26.3
./gradlew :26.3:build :26.3:copyBuiltJarToOutputs

# Build for Minecraft 26.2
./gradlew :26.2:build :26.2:copyBuiltJarToOutputs
```

---

## 📄 License

This mod is available under the MIT License.