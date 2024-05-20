# Munera: Expense Tracking Application

## Overview

Munera is an application designed to help users efficiently track their expenses. The backend of the application is currently functional, allowing users to perform CRUD (Create, Read, Update, Delete) operations on expenses, creditors, debtors, and categories. The application is built on a PostgreSQL database to securely store expense-related information.

### 1. Expense Management

- Create, read, update, and delete expenses with the following details:
    - Name
    - Date
    - Category
    - Cost
    - Description
    - Period Interval (1, 2, 3)
    - Period Unit (days, weeks, months)
    - Creditors and debtors

## Completed Features

- [x] Create concept of creditors and debtors for each expense
- [x] Insert expenses of the past and of the future
- [x] Have an option to set up recurring expenses

## Next Steps

1. **Frontend Structure**
    - Decide on the frontend structure, most likely a Command Line Interface (CLI).

2. **CLI Design**
    - If a CLI is implemented, consider designing tables and a calendar view for enhanced user experience.

3. **Filtering and Sorting**
    - Implement filtering and sorting functionalities for all major entities to enhance data organization.

4. **Weekly and Monthly Summaries**
    - Create functionality to generate weekly and monthly summaries, including filtering and sorting options.

5. **Reports for Creditors and Debtors**
    - Develop reports outlining debts or credits for each creditor and debtor to provide users with a comprehensive overview.
