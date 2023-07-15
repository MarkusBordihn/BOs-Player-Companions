# Changelog for Player Companions

## Note

This change log includes the summarized changes.
For the full changelog, please go to the [Git Hub History][history] instead.

### 2023.07.15

- Refactored code for version 1.20-46.0.14

### 2023.06.26

- Refactored code for version 1.19.4-45.1.2

### 2023.02.27

- Improved lighting logic for player companions to avoid flickering and garbage collection issues.
- Fixed issue with `respawnOnDeath=false` option.

### 2023.02.24

- Moved texture related operations to client side.
- Improved overall server and network performance by 10%.

### 2023.02.02

- Refactored code for version 1.19.3-44.1.8

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

### 2022.08.07

- Refactored code for version 1.19.2-43.0.0

### 2022.08.03

- Added new raptor player companion.

### 2022.07.28

- Refactored code for version 1.19.1-42.0.0

### 2022.07.22

- Added better compatibility with twitchspawn to allow directly spawn of companions with pre-define names.
- Removed duplicated storing of companion name and will use the standard custom name instead.
- Added additional goal to let untamed companions more moving around the world.

### 2022.07.12

- Refactored code for version 1.19-41.0.96

### 2022.07.09

- Fixed changes with 1.19-41.0.79 release.

### 2022.06.17

- Added new lizard player companion.
- Added crafting recipe for patchouli player companions book.
- Reworked snail textures.
- Fixed ConfigValue#get() change.

### 2022.05.15

- Refactored code for 1.19-41.0.27

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
