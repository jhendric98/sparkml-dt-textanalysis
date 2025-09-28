"""Download and clean the SMS Spam Collection dataset.

The script fetches the official archive from the UCI Machine Learning Repository,
extracts the TSV file and performs minimal normalisation to guarantee compatibility
with the Spark ML pipeline.
"""

from __future__ import annotations

import argparse
import csv
import io
import sys
import textwrap
import zipfile
from pathlib import Path
from typing import Iterable

import requests

DATASET_URL = "https://archive.ics.uci.edu/ml/machine-learning-databases/00228/smsspamcollection.zip"
ZIP_MEMBER = "SMSSpamCollection"


def parse_args(argv: Iterable[str] | None = None) -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description="Download the SMS Spam Collection dataset and export it as TSV.",
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog=textwrap.dedent(
            """\
            The output format matches the expectations of SparkMlDecisionTreeApp.
            Example usage:

              uv run download-sms-spam --output data/SMSSpam.tsv
            """
        ),
    )
    parser.add_argument(
        "--output",
        type=Path,
        required=True,
        help="Destination TSV file path.",
    )
    parser.add_argument(
        "--force",
        action="store_true",
        help="Overwrite the output file if it already exists.",
    )
    return parser.parse_args(argv)


def download_archive() -> bytes:
    response = requests.get(DATASET_URL, timeout=60)
    response.raise_for_status()
    return response.content


def extract_tsv(archive: bytes) -> Iterable[tuple[str, str]]:
    with zipfile.ZipFile(io.BytesIO(archive)) as zf:
        with zf.open(ZIP_MEMBER) as member:
            reader = csv.reader(io.TextIOWrapper(member, encoding="utf-8"), delimiter="\t")
            for row in reader:
                if len(row) != 2:
                    continue
                label, message = row
                yield label.strip().lower(), " ".join(message.split())


def save_tsv(rows: Iterable[tuple[str, str]], destination: Path, force: bool) -> None:
    destination.parent.mkdir(parents=True, exist_ok=True)
    if destination.exists() and not force:
        raise FileExistsError(
            f"Destination {destination} already exists. Pass --force to overwrite."
        )

    with destination.open("w", newline="", encoding="utf-8") as handle:
        writer = csv.writer(handle, delimiter="\t")
        for label, message in rows:
            writer.writerow([label, message])


def main(argv: Iterable[str] | None = None) -> int:
    args = parse_args(argv)
    try:
        archive = download_archive()
        rows = list(extract_tsv(archive))
        if not rows:
            raise RuntimeError("The downloaded archive did not contain any valid rows.")
        save_tsv(rows, args.output, args.force)
    except Exception as exc:  # pragma: no cover - handled for CLI UX
        print(f"Error: {exc}", file=sys.stderr)
        return 1
    else:
        print(f"Saved {len(rows)} rows to {args.output}")
        return 0


if __name__ == "__main__":  # pragma: no cover
    sys.exit(main())
