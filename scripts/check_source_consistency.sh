#!/usr/bin/env bash
set -euo pipefail

if [ -f app/src/main/java/com/petlingo/app/data/PetData.kt ]; then
  echo "PetData.kt 是舊版範例資料，使用不相容的資料模型，請刪除。"
  exit 1
fi

MODEL_FILE="app/src/main/java/com/petlingo/app/model/Models.kt"

for model in Word QuizQuestion QuizSession AnswerRecord ReadingPassage ReadingSession WrongAnswer SpeakingRecord; do
  if ! grep -q "data class $model" "$MODEL_FILE"; then
    echo "Models.kt 缺少 $model"
    exit 1
  fi
done

echo "Source consistency check passed."
