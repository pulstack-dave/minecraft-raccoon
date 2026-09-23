# Installing Minecraft Raccoon

This guide provides clear, practical instructions to get the **Minecraft Raccoon** plugin up and running on your Minecraft server with minimal friction and maximum benefit for you and your players.

---

### Prerequisites

To achieve the best performance, stability, and compatibility across your player base:
- **Server Software:** A **Paper**, **Purpur**, or **Spigot** server running Minecraft **1.20.x** (Paper 1.20.4+ recommended for optimal performance).
- **Java Runtime:** **Java 17** or newer.
- **Client Requirements:** None! Because this is a server-side plugin, connecting players do not need to install any client-side mods to experience and interact with raccoons.

---

### Step 1: Build or Obtain the Plugin JAR

If you are compiling from source to ensure you have the latest build:

1. Open a terminal in the project root directory.
2. Run the Maven packaging command:
   ```bash
   mvn clean package
   ```
3. Once completed, your compiled plugin JAR will be located in the `target/` directory:
   ```
   target/minecraft-raccoon-1.0.0.jar
   ```

---

### Step 2: Add the Plugin to Your Server

1. Locate your Minecraft server's root folder.
2. Open the `plugins/` directory inside your server folder (create it if it does not already exist).
3. Copy or move `minecraft-raccoon-1.0.0.jar` into the `plugins/` directory.

```text
your-minecraft-server/
├── plugins/
│   └── minecraft-raccoon-1.0.0.jar
├── server.properties
└── paper.jar (or spigot.jar)
```

---

### Step 3: Start the Server

1. Start (or restart) your server using your usual startup script:
   ```bash
   java -Xms2G -Xmx4G -jar paper.jar --nogui
   ```
2. During startup, the plugin will initialize, register raccoon entities and AI listeners, and automatically generate the default configuration file at:
   ```
   plugins/MinecraftRaccoon/config.yml
   ```

---

### Step 4: Verify the Installation

1. In the server console or in-game (as an OP / administrator), run:
   ```
   /plugins
   ```
   Confirm that **MinecraftRaccoon** is listed in green.
2. Check plugin status and access commands using:
   ```
   /raccoon
   ```
   or its alias:
   ```
   /rc
   ```
3. Give yourself a Raccoon Spawn Egg to test:
   ```
   /raccoon egg
   ```
   Right-click a block or throw the egg to hatch a baby raccoon.

---

### Configuration & Optimization

To fine-tune gameplay mechanics to best serve your community:
- Open `plugins/MinecraftRaccoon/config.yml`.
- Adjust growth speeds, hunting behaviors, container rummaging, and favorite foods.
- Apply your changes live without restarting the server by running:
  ```
  /raccoon reload
  ```
