# Changelog for Player Companions

## Note

This change log includes the summarized changes.
For the full changelog, please go to the [Git Hub History][history] instead.

### 2023.11.19

- Added additional checks for tamed player companions items to make sure they are not getting lost with specific inventory mods.
- Added basic German translation.

### 2023.10.12

- Fixed issue with custom url skins not working correctly.

### 2023.10.11

- Added advancements for player companions from crafting the tame items to taming the different companion.
- Added automated recipe granting and notification for tame items.
- Added help text for untamed companions for the right tame item.
- Improved network performance for player companions.

### 2023.10.10

- Added new raptor variants.
- Fixed issue were missing sounds could cause a crash. #27
- Fixed issue were baby Raptor looks decapitated when sitting. #27

### 2023.08.11

- Added smaller bug fixes.
- Fixed Small Ghast could not fly. #21
- Fixed Snails spawn on the sea and stay on top. #23
- Fixed Server crasher like #24

### 2023.02.27

- Improved lighting logic for player companions to avoid flickering and garbage collection issues.
- Fixed issue with `respawnOnDeath=false` option.

### 2023.02.24

- Moved texture related operations to client side.
- Improved overall server and network performance by 10%.

### 2023.02.01

- Added additional on-screen messages after successful capturing a player companion.

### 2022.11.26

- Added `/player_companion summon` command to summon "lost" player companions.
- Added new UI element to display basic information and possibility to control the companion (wip).
- Fixed smaller edge cases and added additional client and debug messages.

### 2022.08.18

- Added possibility to link empty companion item to existing companion.

### 2022.08.14

- Added pink raptor variant and fixed head animation.
- Improved automatic data backup with additional options.

### 2022.08.03

- Added new raptor player companion.

### 2022.07.23

- Improved probability of fairy spawning.

### 2022.07.22

- Added better compatibility with twitchspawn to allow directly spawn of companions with pre-define names.
- Removed duplicated storing of companion name and will use the standard custom name instead.
- Added additional goal to let untamed companions more moving around the world.

### 2022.06.17

- Added new lizard player companion.
- Added crafting recipe for patchouli player companions book.
- Reworked snail textures.

### 2022.05.14

- Fixed missing description text for tame items.
- Fixed missing ui background.
- Added alternative design for spawn eggs.
- Optimized code.

### 2022.05.13

- Fixed missing particle animations for feeding player companions
- Fixed crash with creative companion items
- Removed duplicated call of rendering the background for the UI

### 2022.05.01

- Finalized first release candidate.
- Unified companions for easier integration and maintenance.

### 2022.04.22

- Added idle status which improves performance by 20%-50% for unowned player companions.
- Added config options to allow customized list of names
- Fixed smaller issues with docker based dedicated server and fetching textures
- Fixed issue with owner is not displayed correctly
- Allowing changing the custom skin only every 30 secs

### 2022.04.21

- Ported all changes from 1.18.1 to 1.18.2
- Updated dependencies to the latest versions
- Refactored code for 1.18.2-40.1.0

### 2022.04.20

- Optimized network protocols to avoid large package message, even with 100 companions on a single player.

[history]: https://github.com/MarkusBordihn/BOs-Player-Companions/commits/
