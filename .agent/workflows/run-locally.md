---
description: Update branch with main, run tests and build to ensure local environment is healthy
---

// turbo-all

1. Fetch and merge main:

```powershell
git fetch origin main && git merge origin/main
```

1. Run tests:

```powershell
./gradlew test --console=plain
```

1. Run build:

```powershell
./gradlew build -x test --console=plain
```

1. If all commands succeed, notify the user that everything is correct.
