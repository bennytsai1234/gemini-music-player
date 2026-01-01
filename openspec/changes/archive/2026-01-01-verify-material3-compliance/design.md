# Design: Material 3 Compliance Strategy

## Design System Architecture

The project follows a centralized design system in `core/designsystem`.
We enforce Material 3 via `androidx.compose.material3`.

### Theme Mapping

- **Colors**: We map `Gemini*` brand colors to M3 `ColorScheme` roles (`primary`, `onPrimary`, `container`, etc.).
- **Typography**: We map custom fonts and weights to M3 `Typography` roles (`labelLarge`, `bodyMedium`, `headlineSmall`).

### Component Guidelines

- Avoid `MaterialTheme.colors` (M2).
- Use `MaterialTheme.colorScheme`.
- Use `MaterialTheme.typography` with M3 names.
- Prefer M3 specific components (`NavigationBar` over `BottomNavigation`).

### Dynamic Color Policy

- Currently disabled to ensure brand consistency.
- **Decision**: Keep disabled by default unless user explicitly enables "System Colors" in settings. We will verify the `dynamicColor` toggle in `GeminiTheme`.
