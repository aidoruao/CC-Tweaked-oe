# Server Admin Guide — Detecting CC:Tweaked OE Fork

## What This Fork Adds

This fork adds `turtle.activate()`, `turtle.activateUp()`, and `turtle.activateDown()` — methods that allow ComputerCraft turtles to right-click any block. This capability was previously unavailable in vanilla CC:Tweaked.

## Version Fingerprint

| Version | Fork | Has `turtle.activate()`? |
|---------|------|--------------------------|
| `computercraft:1.118.0` | **CC:Tweaked OE** (this fork) | YES |
| `computercraft:1.116.2` and below | Vanilla CC:Tweaked | NO |

The OE fork is the **only** version of CC:Tweaked that reports version `1.118.0`. Any player connecting with `computercraft:1.118.0` has the right-click capability.

## Detection Methods

### Method 1: Mod List Inspection (Server-Side)

The Forge server handshake sends the client's mod list to the server. Server plugins can inspect this list:

```java
// Example: Check if a player has the OE fork
ModList modList = player.getModList();
for (ModInfo mod : modList.getMods()) {
    if (mod.getModId().equals("computercraft") && mod.getVersion().equals("1.118.0")) {
        // Player has OE fork with turtle.activate()
    }
}
Method 2: Lua Runtime Detection
A server-side ComputerCraft program can check if turtle.activate exists:

lua
-- Run this on any turtle to check
if turtle.activate then
    print("OE FORK DETECTED — turtle.activate() available")
else
    print("VANILLA CC:TWEAKED")
end
Method 3: Network Packet Inspection
turtle.activate() sends the same network packets as turtle.place(). If a turtle interacts with a block type that vanilla CC:Tweaked cannot interact with (crafting tables, furnaces, anvils, enchanting tables), and turtle.use() is not present but the interaction succeeds, the player is using the OE fork.

Recommended Server Policy
The turtle.activate() method is a tool. It does not automatically grief. It does not bypass server permissions. It does not disable anti-grief plugins. The same moderation tools that work for vanilla turtles work for OE turtles.

Recommended approach:

Do nothing. Treat OE turtles the same as vanilla turtles. Griefers get banned. Builders build.

Log it. Use the version detection to log which players use the OE fork. No action, just awareness.

Restrict it. If your server wants to block the OE fork, reject connections from clients with computercraft:1.118.0. This is no different from blocking any other mod version.

Technical Note

Generated from CC:Tweaked OE fork — https://github.com/aidoruao/CC-Tweaked-oe
