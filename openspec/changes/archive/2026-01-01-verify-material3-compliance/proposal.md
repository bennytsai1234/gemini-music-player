# Change: Verify and Enforce Material 3 Compliance

## Why
To audit the current codebase for strict adherence to Material 3 design guidelines and implementation standards, ensuring a consistent, premium, and future-proof UI.

## Context
The project has migrated to strict Material 3 (Compose M3), but a comprehensive audit is needed to verify:
1.  No legacy Material 2 usages remain (verified by initial grep, but manual review needed).
2.  Theme definitions (`Theme.kt`) correctly map to M3 `ColorScheme` and `Typography`.
3.  Custom components in `GeminiComponents.kt` leverage M3 tokens properly.
4.  Dynamic color support is configured correctly (currently disabled by default).

## What Changes
1.  **Audit**: Review `core/designsystem` for semantic token usage.
2.  **Refine**: Update `Theme.kt` and `Type.kt` (if needed) to fully valid M3 scales.
3.  **Document**: Create a persistent spec for UI Theming to prevent regression.
