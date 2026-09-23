### Installing Minecraft Raccoon Mod

This guide provides practical instructions to install and use the **Minecraft Raccoon** mod on your client or server using **Fabric** or **NeoForge**.

---

### Prerequisites

To maximize gameplay performance, compatibility, and ease of use:
- **Minecraft Version:** `1.20.4` or above (`>=1.20.4`).
- **Mod Loader:** **Fabric Loader** (`>=0.15.0`) with **Fabric API**, or **NeoForge** (with compatibility layer / Sinytra Connector for Fabric mods).
- **Java Runtime:** `Java 17` or newer.

---

### Step 1: Download or Build the Mod JAR

If compiling from source:
1. Open a terminal in the project directory.
2. Run the Gradle build task:
   ```bash
   ./gradlew build
   ```
3. Your compiled mod JAR will be located in the `build/libs/` folder:
   ```text
   build/libs/minecraft-raccoon-1.0.0.jar
   ```

---

### Step 2: Install into Your Minecraft `mods` Folder

1. Open your local Minecraft installation directory:
   - **Windows:** `%appdata%\.minecraft\mods`
   - **macOS:** `~/Library/Application Support/minecraft/mods`
   - **Linux:** `~/.minecraft/mods`
2. Place `minecraft-raccoon-1.0.0.jar` into the `mods` folder.
3. For Fabric, ensure `fabric-api.jar` matching your Minecraft version is also present in the `mods` folder.

```text
.minecraft/
└── mods/
    ├── fabric-api-0.97.0+1.20.4.jar
    └── minecraft-raccoon-1.0.0.jar
```

---

### Step 3: Finding Your Raccoon in Creative Mode

1. Launch Minecraft using your Fabric or NeoForge profile.
2. Enter any Singleplayer world or multiplayer server running the mod.
3. Open your Creative inventory (`E`) and navigate to the **Spawn Eggs** category tab.
4. Pick up the **Raccoon Spawn Egg** and right-click on any block to spawn your raccoon.

---

### Crafting Recipe (Survival Mode)

You can craft a Raccoon Spawn Egg in Survival Mode using a shapeless crafting recipe in a standard Crafting Table:
- `1x` `Egg`
- `1x` `Sweet Berries`
- `1x` `Bread`
- `1x` `Raw Cod` (or `Raw Salmon`)

---

### Troubleshooting

1. **Egg not showing up in Creative Spawn Eggs tab or search:**
   - **Fabric:** Ensure `fabric-api.jar` is in your `.minecraft/mods` directory. Fabric API is required for registering creative tab entries and entity attributes.
   - **NeoForge:** If running on NeoForge (e.g. Enhanced MC / NeoForge modloader), ensure you have **Sinytra Connector** and **Forgified Fabric API** installed in your `.minecraft/mods` folder so Fabric mod entrypoints and registries execute properly.
2. **Crafting Recipe doesn't craft the egg:**
   - Verify all 4 ingredients in a Crafting Table: `1x Egg`, `1x Sweet Berries`, `1x Bread`, and `1x Raw Cod` (or `1x Raw Salmon`).
   - If the recipe still doesn't craft, check that the mod initialized properly (and didn't skip initialization due to missing Fabric API / Sinytra Connector).

---

### Taming, Training, and Poses (Wolf-Style)

1. **Standing on All 4s (Walking / Exploring):**
   - Wild and standing raccoons roam around, sniff containers, and hunt prey (chickens, rabbits).
2. **Standing Up on 2 Hind Legs (Begging / Curious):**
   - When a player holds favorite treats (`Sweet Berries`, `Glow Berries`, `Bread`, `Apple`, or `Fish`) within 8 blocks, wild and standing raccoons stand upright on two hind legs and look at the player with raised paws.
3. **Sitting Down (Commanded Pose):**
   - Right-click your tamed raccoon with an empty hand to toggle sitting down or following you.
4. **Taming & Bonding:**
   - Right-click a wild raccoon with `Sweet Berries`, `Glow Berries`, `Bread`, or `Fish` to tame it. Successful taming emits heart particles.
5. **Combat & Defense:**
   - Tamed raccoons defend their owner from hostile attackers and assist when their owner strikes a mob.
6. **Healing & Breeding:**
   - Feed food to an injured tamed raccoon to restore its health.
   - Feed two adult tamed raccoons at full health to breed a baby raccoon.
