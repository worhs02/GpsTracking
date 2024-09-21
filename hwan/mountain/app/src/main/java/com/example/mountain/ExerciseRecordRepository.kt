package com.example.mountain

object ExerciseRecordRepository {
    private val records = mutableListOf<ExerciseRecord>()

    fun saveRecord(record: ExerciseRecord) {
        records.add(record)
    }

    fun getRecords(): List<ExerciseRecord> {
        return records
    }
}
