# Shared Frontend Specification

## Purpose

This document defines project-wide frontend presentation and interaction standards shared across Trading Journal features. It provides durable guardrails for a consistent React, TypeScript, Vite, and Material UI application without defining feature behavior or a custom design system.

## Scope

This specification covers:

- the overall application shell and page layout;
- spacing, alignment, and typography;
- consistent Material UI usage;
- shared form, button, card, panel, and result patterns;
- responsive desktop-friendly behavior;
- accessibility;
- loading, success, validation, and technical-error presentation;
- visual consistency across future features.

It does not define:

- feature-specific fields or validation rules;
- API contracts;
- business calculations or backend behavior;
- feature-specific acceptance criteria.

Feature-specific artifacts remain authoritative for those concerns.

## Visual Direction

The application should have a clean, modern, professional financial-product style. It should look intentionally composed rather than like unarranged default Material UI components while remaining simple and restrained rather than decorative or flashy.

Interfaces should prioritize readability, clear hierarchy, whitespace, and meaningful grouping. Avoid oversized empty areas, dense walls of controls, unnecessarily full-width forms, and inconsistent spacing or alignment. This specification does not introduce a brand identity, custom color system, font family, or elaborate design system.

## Application Layout

- Center primary page content within a reasonable maximum width on desktop.
- Use responsive horizontal padding and avoid content touching viewport edges.
- Establish a clear hierarchy among page title, supporting text, sections, and actions.
- Group related controls and results so their relationships are immediately understandable.
- Keep form and result widths appropriate to their contents rather than stretching them across the viewport.
- Allow a reusable shell to accommodate navigation or a header as future features require them.
- Do not add unnecessary navigation while Feature 001 is the only implemented screen.

## Material UI Usage

- Use Material UI components consistently and preserve their accessibility behavior.
- Prefer theme-level values and reusable shared patterns over scattered one-off styling.
- Compose components into an intentional layout rather than relying on raw defaults.
- Reuse shared styling decisions across future features.
- Avoid custom CSS when Material UI composition or theming expresses the design cleanly.
- Do not add frontend dependencies beyond the approved stack without separate approval.

## Typography

Maintain a readable, professional hierarchy for:

- application and page titles;
- section headings;
- body and supporting text;
- labels and helper text;
- validation and error text;
- important result labels and values.

Heading levels must reflect document structure. Result values may receive stronger emphasis, but supporting information should remain legible without competing with primary content.

## Spacing and Alignment

Use a consistent theme-based spacing rhythm between page sections, form fields, labels and helper text, action groups, and result areas. Prefer shared layout primitives and aligned edges over arbitrary margins. Related items should be closer to one another than unrelated sections.

## Forms

- Use clear, persistent labels and sensible field widths.
- Arrange related fields in responsive rows or grids on wider screens when this improves scanning.
- Let rows stack naturally on narrower screens.
- Make required, invalid, disabled, and loading states visually clear.
- Place validation feedback near the affected field when appropriate.
- Give submission controls an obvious hierarchy.
- Prevent accidental duplicate interaction while an active submission is being processed when appropriate.
- Keep loading and disabled states stable so controls do not cause avoidable layout jumps.

Feature-specific specifications define the actual fields and validation behavior.

## Buttons and Actions

Use one visually clear primary form action where appropriate. Secondary actions must be visually subordinate. Maintain consistent sizing, alignment, and spacing, and ensure loading or disabled behavior does not unexpectedly change control dimensions or surrounding layout.

## Cards, Panels, and Results

Use cards or panels when they improve grouping and comprehension; avoid excessive or deeply nested containers. Forms and result areas should have distinct, reusable presentation patterns. Important result values require clear hierarchy.

Positive, negative, and neutral financial outcomes may be visually differentiated, but their meaning must also be communicated through text, values, or another non-color cue.

## Loading, Success, and Error States

- Make active loading or submission states obvious.
- Provide visible confirmation after successful actions.
- Present validation errors in understandable language near affected controls where practical.
- Show technical failures clearly without exposing internal implementation details.
- Keep state transitions spatially stable and avoid unnecessary layout movement.
- Never display a success state for a failed operation.

## Responsive Behavior

The application is desktop-friendly first but must remain usable on narrower screens. Rows and grids may stack vertically as space decreases. Normal form usage must not require horizontal scrolling, and controls, labels, messages, and results must remain readable and operable.

## Accessibility

- Associate controls with semantic labels.
- Preserve keyboard operation and visible focus states.
- Maintain sufficient text and control contrast.
- Do not communicate validation or financial meaning through color alone.
- Use a sensible heading hierarchy.
- Preserve Material UI accessibility features rather than bypassing them with custom behavior.

## Consistency Rules

Future features should reuse the established theme, application layout, spacing rhythm, typography hierarchy, form patterns, button hierarchy, and loading/success/error presentation. Feature specifications may extend these patterns for an approved need but must not silently contradict shared conventions.

## Feature 001 Guidance

Feature 001 applies these shared standards to its completed-trade entry form, submission state, validation and error presentation, and calculated-result presentation. Its own Specification, Plan, Tasks, and OpenAPI contract remain authoritative for fields, calculations, precision, validation, and API behavior.

## Evolution

Feature 001 establishes the first concrete implementation of these standards. Reusable patterns may be refined after implementation based on observed usability and visual quality. Broader frontend changes may later justify shared artifacts such as `ui-flow.md`, `plan.md`, or `tasks.md`; those artifacts are not required at this stage.

## Authority and Conflict Rule

This shared specification is authoritative for project-wide frontend presentation and interaction conventions. Individual feature specifications remain authoritative for feature behavior.

This document must not override business rules, API contracts, or feature acceptance criteria. If a conflict appears, preserve approved feature behavior, surface the conflict, and explicitly reconcile the shared frontend standard before proceeding.
