# Contributing to Spring Boot Database Validator

We ❤️ contributions! Whether it’s bug fixes, new features, documentation improvements, or reporting issues, your help is always welcome.

---

## Reporting Issues

If you find a bug or have a feature request:

1. Check if a similar issue already exists.
2. If not, create a **new issue** on GitHub.
3. Include **logs, screenshots, or steps to reproduce** the problem. The more details, the faster it can be addressed.

---

## Discuss Before Coding

- Before starting work on a new feature or bug fix, **comment on the issue** to discuss your approach with the maintainers.
- This avoids duplication of effort and ensures your work aligns with project goals.

---

## Fork and PR Workflow

1. **Fork** the repository.
2. **Clone** your fork locally
3. **Create** a new branch for your feature or fix:
4. Make your changes following the project’s code style.
5. Test your changes to make sure nothing breaks.
6. Push your branch to your fork
7. Submit a Pull Request (PR) against `feature/develop`.
8. PRs should be clear and focused. Reference the related issue in your PR description. (By adding the issue number eg: `#12`)

```bash
git clone https://github.com/your-username/database-validator.git
git checkout -b feature/my-new-feature
git push origin feature/my-new-feature
```

## Guidelines

- Follow Java and Spring Boot coding conventions.
- Write meaningful commit messages.
- Keep changes atomic (one feature/fix per PR).
- Add unit tests for new functionality wherever possible.

## Thank you

Your contributions make this project better for everyone. Thank you for helping the community!


# To check for linting errors and formatting issues (does not fix them)
npm run lint:check
npm run format:check

# To automatically fix linting errors and format code
npm run lint
npm run format