# Changelog for Player Companions

## Note

This change log includes the summarized changes.
For the full changelog, please go to the [Git Hub History][history] instead.

### 2022.08.03

- Added new raptor player companion.

### 2022.07.22

- Added better compatibility with twitchspawn to allow directly spawn of companions with pre-define names.
- Removed duplicated storing of companion name and will use the standard custom name instead.
- Added additional goal to let untamed companions more moving around the world.

### 2022.07.12

- Refactored code for version 1.19-41.0.96.

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
