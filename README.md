# HavenElytra
![Versions](https://img.shields.io/badge/Versions-1.18%20--%201.19%2B-brightgreen?style=flat)
<a href="https://github.com/Valorless/ValorlessUtils" rel="nofollow"><img src="https://img.shields.io/badge/Requires-ValorlessUtils-red?style=flat" alt="Dependency" style="max-width: 100%;"/>
<br>

## Commands
| Command | Description |
| --- | --- |
| `/havenelytra` | Opens the main GUI menu. |
| `/havenelytra reload` | Reloads config.yml |

*All commands can be shortened to /he*
  
## Permissions
| Permission | Description |
| --- | --- |
| `havenelytra.*` | Gives all HavenElytra permissions. |
| `havenelytra.reload` | Allows usage of /havenelytra reload. |
| `havenelytra.combine` | Allows you to combine elytras. |
| `havenelytra.separate` | Allows you to separate elytras. |

## Configuration
| Config Entry | Description | Default | 
| --- | :---: | :---: |
| `combine` | Whether chestplates can be combined with elytras. | true |
| `separate` | Whether elytras can be seperated back to chestplate and elytra. | true |

## Main GUI

| Config Enty | Description |
| --- | --- |
| `sound` | Sound which is supposed to be played when a button is pressed.<br>But as I am an idiot, i forgot to add this. |
| `gui-name` | Name of the GUI. |
| `gui-size` | The amount of slots in the GUI container. |
| `gui` | Slots defined. |
| `filler` | |

| GUI Slot Variable | Description |
| --- | --- |
| `name` | Name of the slot's item. |
| `item` | Item material. |
| `lore` | Item lore. |
| `interact` | Whether the item in this slot can be moved. |
| `tag` | Used to identify the buttons. |
| `custom-model-data` |  |

| GUI Tags | Description |
| --- | --- |
| `COMBINE` | Marks this slot as a button to open the Combine menu. |
| `SEPARATE` | Marks this slot as a button to open the Separate menu. |

<details>
  <summary>GUI Example:</summary>

```yaml
main-gui:
  '12':
    name: §dCombine
    item: ANVIL
    lore:
      - '§fLeft-Click to open the combination menu.'
    interact: false
    tag: COMBINE
  '14':
    name: §bSeparate
    item: GRINDSTONE
    lore:
      - '§fLeft-Click to open the separation menu.'
    interact: false
    tag: SEPARATE

```
You can copy and use this as default.<br>
The reason this is not default, is that if you don't define the ones default in the config file (I removed them all to avoid this),
then any undefined slots would default to the plugin's internal default config.
</details>

## Combine GUI

| Config Enty | Description |
| --- | --- |
| `combine-success` | Message sent to the player when the item is combined. |
| `combine-fail` | Message sent to the player when no elytra, or a combined elytra is used. |
| `sound` | Sound which is played when the item is combined. |
| `gui` | Slots defined. |
| `filler` | |

| GUI Slot Variable | Description |
| --- | --- |
| `name` | Name of the slot's item. |
| `item` | Item material. |
| `lore` | Item lore. |
| `interact` | Whether the item in this slot can be moved. |
| `tag` | Used to identify the buttons. |
| `custom-model-data` |  |

| GUI Tags | Description |
| --- | --- |
| `PLAYER` | Slot for the player's item. return these slots to the player upon closing inventory.<br>!IMPORTANT only 2 of these, always chestplate first. |
| `CONFIRM` | Marks this slot as a button to confirm merging. |

<details>
  <summary>GUI Example:</summary>

```yaml
combine-gui:
  '12':
    name: §e§lInfo
    item: IRON_CHESTPLATE
    lore:
      - '§fPlace your chestplate in the'
      - '§fempty slot underneath.'
    interact: false
    tag: ''
  '14':
    name: §e§lInfo
    item: ELYTRA
    lore:
      - '§fPlace the elytra you wish'
      - '§fto merge with.'
    interact: false
    tag: ''
  '21':
    name: Slot 1
    item: AIR
    lore:
      - ''
    interact: true
    tag: PLAYER
  '23':
    name: Slot 2
    item: AIR
    lore:
      - ''
    interact: true
    tag: PLAYER
  '31':
    name: §a§lMerge
    item: LIME_STAINED_GLASS_PANE
    lore:
      - '§fClick here to'
      - '§fcombine the items.'
    interact: false
    tag: CONFIRM

```
You can copy and use this as default.<br>
The reason this is not default, is that if you don't define the ones default in the config file (I removed them all to avoid this),
then any undefined slots would default to the plugin's internal default config.
</details>

## Separate GUI

| Config Enty | Description |
| --- | --- |
| `separate-success` | Message sent to the player when the item is separated. |
| `separate-fail` | Message sent to the player when no elytra, or a non-combined elytra is used. |
| `sound` | Sound which is played when the item is separated. |
| `gui` | Slots defined. |
| `filler` | |

| GUI Slot Variable | Description |
| --- | --- |
| `name` | Name of the slot's item. |
| `item` | Item material. |
| `lore` | Item lore. |
| `interact` | Whether the item in this slot can be moved. |
| `tag` | Used to identify the buttons. |
| `custom-model-data` |  |

| GUI Tags | Description |
| --- | --- |
| `PLAYER` | Slot for the player's item. return these slots to the player upon closing inventory.<br>!IMPORTANT only 2 of these, always elytra first. |
| `CONFIRM` | Marks this slot as a button to confirm merging. |

<details>
  <summary>GUI Example:</summary>

```yaml
separate-gui:
  '12':
    name: §e§lInfo
    item: ELYTRA
    lore:
      - '§fPlace your elytra in the'
      - '§fempty slot to the right.'
    interact: false
    tag: ''
  '13':
    name: Slot 1
    item: AIR
    lore:
      - ''
    interact: true
    tag: PLAYER
  '22':
    name: §c§lSeperate
    item: RED_STAINED_GLASS_PANE
    lore:
      - '§fClick here to'
      - '§fseperate the items.'
    interact: false
    tag: CONFIRM
  '31':
    name: Slot 2
    item: AIR
    lore:
      - ''
    interact: true
    tag: PLAYER

```
You can copy and use this as default.<br>
The reason this is not default, is that if you don't define the ones default in the config file (I removed them all to avoid this),
then any undefined slots would default to the plugin's internal default config.
</details>
