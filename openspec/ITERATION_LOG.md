# Iteration Log

## 2026-01-21: Pulse Data & Connect (Part 1 Completed)
- **Features Delivered**:
    - **Backup & Restore**: Full JSON backup of playlists, favorites, history, and stats. Supports local file export/import (SAF) and Google Drive integration structure.
    - **Song Blacklist**: Persistent blacklist for files and folders with recursive filtering during scanning.
    - **M3U Support**: Import and Export of standard .m3u playlists.
    - **Architecture**:
        - Added `BlacklistDao` and `BlacklistRepository`.
        - Refactored `MusicRepository` scanning logic for efficiency.
        - Implemented `M3UParser` utility.

## 2026-01-21: Pulse Ultimate (Completed)
- **Features Delivered**:
    - **Smart Playlists**: "Most Played", "Recently Added", "Favorites", "Never Played" dynamic generation.
    - **Lyrics**: Synced lyrics (LRC) support + Online fetching (LrcLib).
    - **Tag Editor**: Full ID3 tag editing support (JAudioTagger).
    - **Audio Effects**: Equalizer, Bass Boost, Virtualizer, Loudness Enhancer UI & Logic.
    - **Smart Shuffle**: Weighted shuffle based on play count (Added to Option Menu).
    - **Gapless/Crossfade**: Polished and production-ready.

- **Technical Debt Paid**:
    - Refactored `AudioEffectController` to Domain layer.
    - Standardized `MusicController` interface.
    - Cleaned up duplicate mappers.

---

# Refinement Iteration Log (Archived)
...
