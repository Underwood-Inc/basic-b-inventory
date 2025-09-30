# Magical Storage Mod for Minecraft 1.21.9

A magical storage mod that provides an interface block for accessing inventories with various upgrade options.

## Features

### Magical Storage Interface
- Basic magical storage interface block
- Can be upgraded with Ender Pearls to increase range
- Default range: 5 blocks, can be upgraded up to 10 times
- Each Ender Pearl increases range by 2 blocks

### Upgraded Magical Storage Interface
- Enhanced version with remote access capabilities
- Can be upgraded with Ender Pearls (up to 15 times)
- Each Ender Pearl increases range by 3 blocks
- Supports linking to remote inventories using the Magical Wand

### Magical Wand
- Tool for linking inventories to upgraded interfaces
- Costs 1 Eye of Ender per link
- Can link inventories up to 100 blocks away
- Shimmers to indicate its magical nature

### Ritual System
- Upgrade interfaces in place using ritual circles
- Requires 4 Obsidian pillars in a 3x3 pattern around the interface
- Provides significant range and efficiency bonuses
- Supports multiple ritual levels for advanced upgrades

## Crafting Recipes

### Magical Storage Interface
```
 E 
ECE
 E 
```
- E = Ender Pearl
- C = Chest

### Upgraded Magical Storage Interface
```
EEE
EME
EEE
```
- E = Eye of Ender
- M = Magical Storage Interface

### Magical Wand
```
  E
 S 
S  
```
- E = Eye of Ender
- S = Stick

## Usage

1. **Basic Setup**: Craft a Magical Storage Interface and place it near your storage
2. **Range Upgrade**: Right-click with Ender Pearls to increase the interface's range
3. **Advanced Setup**: Upgrade to the Upgraded version for remote access
4. **Remote Linking**: Use the Magical Wand to link distant inventories
5. **Ritual Upgrade**: Build a ritual circle with 4 Obsidian pillars around the interface

## Ritual System

### Basic Ritual
- Place 4 Obsidian blocks in the corners of a 3x3 area around the interface
- Provides +10 range and reduced linking costs

### Advanced Ritual
- Can be performed after the basic ritual
- Provides additional +20 range and minimal linking costs

## Technical Details

- Compatible with Minecraft 1.21.9
- Uses Forge modding framework
- Supports all standard inventory types
- Configurable range and costs
- Persistent storage across game sessions

## Installation

1. Install Minecraft Forge for version 1.21.9
2. Download the mod JAR file
3. Place it in your mods folder
4. Launch Minecraft with the Forge profile

## Development

This mod is built using:
- Minecraft Forge 1.21.9-50.0.0
- Java 21
- Gradle build system

To build from source:
```bash
./gradlew build
```

The compiled JAR will be in `build/libs/`.