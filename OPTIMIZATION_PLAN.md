# Refactoring & Optimization Plan: "Pulse Network & Polish"

## 1. Network & Connectivity (P1)
- [x] **SMB (Samba) Support**: Native SMBv2/v3 support for NAS streaming (Data/Domain/UI implemented).
- [ ] **WebDAV Resume Support**: Allow resuming streams from last position.
- [ ] **Auto-Download Favorites**: Offline cache for starred songs.
- [ ] **Wi-Fi Only Mode**: Setting to restrict streaming/downloading.

## 2. Player Polish (P2)
- [x] **A-B Repeat**: Domain logic implemented (UI pending).
- [ ] **Balance Control**: Left/Right audio balance.
- [ ] **Bluetooth Auto-Resume**: Configurable auto-play.

## 3. UI/UX (P2)
- [ ] **Grid/List Toggle**: View options for Albums/Artists.
- [ ] **Widgets**: 4x1 and 4x2 Homescreen widgets.
- [ ] **Screen Rotation Lock**: Per-app rotation setting.

## 4. Maintenance (Ongoing)
- [ ] **Unit Tests**: Add tests for new Repositories (Blacklist, Backup).
- [ ] **Performance**: Profile list scrolling with large datasets.
