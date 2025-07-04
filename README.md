![CustomNukes Logo](images/customnukes-logo.png)

## Compatibility

Tested on Spigot-1.14, 1.15, 1.16, 1.19, 1.20, 1.20.6, 1.21.

## Introduction

Create your own fully customizable explosives; presets: craft Nuclear, Flash, Normal and Toy bombs.

Hint: increased view-distance and simulation-distance in server.properties improve experience of massive explosions.

Synergies perfectly with [CustomBazookas](https://github.com/uprial/custombazookas/) plugin.

## Screencasts

[![CustomNukes ✩ Bomben mit Einstellungsmöglichkeiten](http://img.youtube.com/vi/6CafoRSWlzA/0.jpg)](http://www.youtube.com/watch?v=6CafoRSWlzA)

[![How to craft bombs in Minecraft with CustomNukes Plugin](http://img.youtube.com/vi/NkgRSTF6yCQ/0.jpg)](http://www.youtube.com/watch?v=NkgRSTF6yCQ)

#### You can configure:
* All the existing recipes or new, your own
* A delay before action, an explosion radius, etc.
* Explosives' base material (sponge by default)
* Action scenario (explosion, potion effect, static repeater, seismic push)
* All other valuable parameters

#### Default set of explosives:

![Toy Bomb](https://raw.githubusercontent.com/uprial/customnukes/master/images/Toy_Bomb.png)

![Bomb](https://raw.githubusercontent.com/uprial/customnukes/master/images/Bomb.png)

![Flash Bomb](https://raw.githubusercontent.com/uprial/customnukes/master/images/Flash_Bomb.png)

![Nuclear Bomb](https://raw.githubusercontent.com/uprial/customnukes/master/images/Nuclear_Bomb.png)

#### A custom nuke may be activated by:

* Red stone right above this block:
![Activate Custom Nuke by Red stone](https://raw.githubusercontent.com/uprial/customnukes/master/images/Activate.png)

* A click via "flint and steel"

* Another explosion, if this block will be destroyed

* An arrow with 'fire'

The main idea to activate explosives is: make a schema of red stones
or a chain of "Toy Bomb" close to main explosive (like "Nuclear Bomb") .

#### A Nuclear Bomb with 120 explosion radius effect:

![A Nuclear Bomb with 120 explosion radius effect](https://raw.githubusercontent.com/uprial/customnukes/master/images/nuke-120r.png)

## Features
* You can assign an infinite number of different actions to one explosive. For example, your "Bomb" can be exploded twice with a small interval between two explosions and glass-blocks will not protect the enemy from this type of explosive.

* You can add several potion effects with different strength, depending on a distance between epicenter and target.

* You can add "static" repeaters to run a scenario every N seconds. For example, add "radiation effect": area with radius 200 where every 2 seconds  all living entities will take a potion effect "poison".

* All special blocks and repeaters will be saved after server reload.

* Additionally, there is a good help in the configuration file (config.yml) and error messages with an explanation of what exactly is wrong in your config (in console logs).

## Commands

* `customnukes reload` - reload config from disk
* `customnukes give @player @explosive-key @amount`
* `customnukes clear` - remove all explosive blocks and active repeaters

## Permissions

* Access to 'reload' command:
`customnukes.reload` (default: op)

* Access to 'give' command:
`customnukes.give` (default: op)

* Access to place, break, craft items of specific explosive type: 
`customnukes.explosive.@explosive-key` (default: op)
This works only if 'check-permissions' is set to 'true' in config.yml.

* Access to 'clear' command:
`customnukes.clear` (default: op)

## Configuration
[Default configuration file](src/main/resources/config.yml)

## Author
I will be happy to add some features or fix bugs. My mail: uprial@gmail.com.

## Useful links
* [Project on GitHub](https://github.com/uprial/customnukes/)
* [Project on Bukkit Dev](http://dev.bukkit.org/bukkit-plugins/customnukes/)
* [Project on Spigot](https://www.spigotmc.org/resources/customnukes.68710/)
* [TODO list](TODO.md)

## Related projects
* CustomBazookas: [Bukkit Dev](https://legacy.curseforge.com/minecraft/bukkit-plugins/custombazookas/) [GitHub](https://github.com/uprial/custombazookas), [Spigot](https://www.spigotmc.org/resources/custombazookas.124997/)
* CustomCreatures: [Bukkit Dev](http://dev.bukkit.org/bukkit-plugins/customcreatures/), [GitHub](https://github.com/uprial/customcreatures), [Spigot](https://www.spigotmc.org/resources/customcreatures.68711/)
* CustomRecipes: [Bukkit Dev](https://dev.bukkit.org/projects/custom-recipes), [GitHub](https://github.com/uprial/customrecipes/), [Spigot](https://www.spigotmc.org/resources/customrecipes.89435/)
* CustomVillage: [Bukkit Dev](http://dev.bukkit.org/bukkit-plugins/customvillage/), [GitHub](https://github.com/uprial/customvillage/), [Spigot](https://www.spigotmc.org/resources/customvillage.69170/)
* MasochisticSurvival: [Bukkit Dev](https://legacy.curseforge.com/minecraft/bukkit-plugins/masochisticsurvival/), [GitHub](https://github.com/uprial/masochisticsurvival/), [Spigot](https://www.spigotmc.org/resources/masochisticsurvival.124943/)
* TakeAim: [Bukkit Dev](https://dev.bukkit.org/projects/takeaim), [GitHub](https://github.com/uprial/takeaim), [Spigot](https://www.spigotmc.org/resources/takeaim.68713/)
