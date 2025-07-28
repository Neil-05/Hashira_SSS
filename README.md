# Hashira_SSS - Shamir's Secret Sharing (Java)

This project implements **Shamir's Secret Sharing** in Java, a cryptographic technique to secure a secret by dividing it into parts (shares). The secret can only be reconstructed using a minimum number of valid shares (threshold `k`). This project also includes support for decoding values from various bases and reconstructing the secret using matrix-based methods (Gaussian Elimination).

## 🔒 Problem Statement

Given a JSON file with the following:
- A set of points `(x, y)` where `y` is encoded in various bases.
- Keys:
  - `n`: Total number of shares
  - `k`: Minimum number of shares required to reconstruct the secret

The goal is to:
1. Decode the values of `y` using the specified base.
2. Use the first `k` points to solve for the coefficients of the polynomial.
3. Recover the constant term (the **secret**) of the polynomial.

---

## 📂 Project Structure

Hashira_SSS/
│
├── gson-2.8.6.jar           # Gson library (required for JSON parsing)
├── input.json               # Input file with shares encoded in various bases
├── ShamirSecretSharing.java  # Main Java implementation
└── README.md                # Project documentation


