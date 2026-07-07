import json
import re
from pathlib import Path

from pypdf import PdfReader

PDF_PATH = Path("2025-Civics-Test-128-Questions-and-Answers.pdf")
OUT_PATH = Path("data/questions_2025.json")

QUESTION_RE = re.compile(r"^(\d+)\.\s{2,}(.+?)\s*$")
PAGE_HEADER_RE = re.compile(r"^\d+ of \d+$")


def is_noise_line(line: str) -> bool:
    s = line.strip()
    if not s:
        return True
    if PAGE_HEADER_RE.match(s):
        return True
    if s.lower() == "uscis.gov/citizenship":
        return True
    return False


def clean_text(text: str) -> str:
    text = text.replace("\uFFFD", "")
    text = text.replace("\u201C", '"').replace("\u201D", '"')
    text = text.replace("\u2018", "'").replace("\u2019", "'")
    text = re.sub(r"\s+", " ", text).strip()
    return text


def is_answer_bullet(line: str) -> bool:
    s = line.lstrip()
    if not s:
        return False
    # Most PDF bullets are rendered as replacement character before text.
    # Accept any non-alnum leading marker followed by a space and text.
    return bool(re.match(r"^[^A-Za-z0-9\(\[]\s*.+", s))


def strip_bullet(line: str) -> str:
    s = line.lstrip()
    s = re.sub(r"^[^A-Za-z0-9\(\[]+\s*", "", s)
    return clean_text(s)


def extract_lines() -> list[str]:
    reader = PdfReader(str(PDF_PATH))
    all_text = "\n".join((page.extract_text() or "") for page in reader.pages)
    return all_text.splitlines()


def build_records(lines: list[str]) -> list[dict]:
    records: list[dict] = []
    current: dict | None = None

    for raw in lines:
        if is_noise_line(raw):
            continue

        line = raw.strip()
        qmatch = QUESTION_RE.match(line)
        if qmatch:
            if current:
                current["question"] = clean_text(current["question"])
                current["acceptedAnswers"] = [clean_text(a) for a in current["acceptedAnswers"] if clean_text(a)]
                records.append(current)

            qid = int(qmatch.group(1))
            qtext = qmatch.group(2)
            is_starred = qtext.endswith("*")
            if is_starred:
                qtext = qtext[:-1].rstrip()

            current = {
                "id": qid,
                "question": qtext,
                "acceptedAnswers": [],
                "isStarred": is_starred,
            }
            continue

        if current is None:
            continue

        # Sometimes the star appears on its own continuation line after the question.
        if line == "*":
            current["isStarred"] = True
            continue

        if is_answer_bullet(line):
            current["acceptedAnswers"].append(strip_bullet(line))
            continue

        # Non-bullet continuation line. Attach to last answer if any,
        # otherwise treat as continuation of question text.
        if current["acceptedAnswers"]:
            current["acceptedAnswers"][-1] = clean_text(current["acceptedAnswers"][-1] + " " + line)
        else:
            current["question"] = clean_text(current["question"] + " " + line)

    if current:
        current["question"] = clean_text(current["question"])
        current["acceptedAnswers"] = [clean_text(a) for a in current["acceptedAnswers"] if clean_text(a)]
        records.append(current)

    return records


def validate(records: list[dict]) -> None:
    if len(records) != 128:
        raise ValueError(f"Expected 128 questions, found {len(records)}")

    ids = [r["id"] for r in records]
    if ids != list(range(1, 129)):
        raise ValueError("Question IDs are not continuous from 1..128")

    missing_answers = [r["id"] for r in records if not r["acceptedAnswers"]]
    if missing_answers:
        raise ValueError(f"Questions missing answers: {missing_answers}")


def main() -> None:
    lines = extract_lines()
    records = build_records(lines)
    validate(records)

    OUT_PATH.parent.mkdir(parents=True, exist_ok=True)
    payload = {
        "source": {
            "title": "128 Civics Questions and Answers (2025 version)",
            "pdf": PDF_PATH.name,
        },
        "totalQuestions": len(records),
        "questions": records,
    }
    OUT_PATH.write_text(json.dumps(payload, indent=2, ensure_ascii=True), encoding="utf-8")

    starred = sum(1 for r in records if r["isStarred"])
    print(f"Extracted {len(records)} questions")
    print(f"Starred questions: {starred}")
    print(f"Output: {OUT_PATH}")


if __name__ == "__main__":
    main()
