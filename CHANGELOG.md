# Changelog for Player Companions

## Note

This change log includes the summarized changes.
For the full changelog, please go to the [Git Hub History][history] instead.

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
