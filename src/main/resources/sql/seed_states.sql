-- Ensures the `states` master table contains every state/UT referenced by the
-- PFMS TSA Expenditure Report ("Status of TSA Expenditure Report PFMS @29-June2026.xlsx").
-- Safe to re-run: only inserts names that are not already present (case-insensitive match).
-- Canonical spellings follow PfmsExpenditureReportService.STATE_NAME_ALIASES
-- (e.g. Haryana not Harayana, Odisha not Orissa, Puducherry not Pondicherry).
--
-- Run manually against the target database (sandbox/prod) — this project has no
-- Flyway/Liquibase migration runner, so nothing executes this automatically.

INSERT INTO states (name)
SELECT x.name
FROM (VALUES
    ('Arunachal Pradesh'),
    ('Tripura'),
    ('Assam'),
    ('Manipur'),
    ('Nagaland'),
    ('Meghalaya'),
    ('Mizoram'),
    ('Sikkim'),
    ('Andhra Pradesh'),
    ('Telangana'),
    ('Himachal Pradesh'),
    ('Madhya Pradesh'),
    ('Tamil Nadu'),
    ('Goa'),
    ('Haryana'),
    ('Jammu And Kashmir'),
    ('Chhattisgarh'),
    ('Kerala'),
    ('Uttarakhand'),
    ('Maharashtra'),
    ('West Bengal'),
    ('Gujarat'),
    ('Puducherry'),
    ('Karnataka'),
    ('Punjab'),
    ('Jharkhand'),
    ('Rajasthan'),
    ('Bihar'),
    ('Uttar Pradesh'),
    ('Delhi'),
    ('Odisha'),
    ('Andaman and Nicobar'),
    ('DNH & DD'),
    ('Ladakh'),
    ('Lakshadweep')
) AS x(name)
WHERE NOT EXISTS (
    SELECT 1 FROM states s WHERE LOWER(s.name) = LOWER(x.name)
);
