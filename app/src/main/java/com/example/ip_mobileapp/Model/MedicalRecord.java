package com.example.ip_mobileapp.Model;

import java.util.List;

public class MedicalRecord
{
    public MedicalRecord() {}

    public MedicalRecord(final User userContext_, List<Allergy> allergies_, List<Examination> examinations_)
    {
        userContext = userContext_;
        allergies = allergies_;
        examinations = examinations_;
    }

    @Override
    public String toString() {
        return "MedicalRecord{" +
                "allergies=" + allergies +
                ", examinations=" + examinations +
                ", userContext=" + userContext +
                '}';
    }

    public List<Allergy> getAllergies()
    {
        return allergies;
    }

    public List<Examination> getExaminations()
    {
        return examinations;
    }

    public void setExaminations(List<Examination> examinations)
    {
        this.examinations = examinations;
    }

    public void setAllergies(List<Allergy> allergies)
    {
        this.allergies = allergies;
    }


    private List<Allergy> allergies;
    private List<Examination> examinations;

    private User userContext;
}