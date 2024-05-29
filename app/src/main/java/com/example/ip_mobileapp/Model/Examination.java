package com.example.ip_mobileapp.Model;


import android.os.Build;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

public class Examination
{
    public Examination() {}

    @Override
    public String toString() {
        return "Examination{" +
                "examinationDate=" + examinationDate +
                ", diagnostic='" + diagnostic + '\'' +
                ", cure='" + cure + '\'' +
                ", recomandation='" + recomandation + '\'' +
                '}';
    }

    public LocalDate getExaminationDate()
    {
        return examinationDate;
    }

    public String getCure()
    {
        return cure;
    }

    public String getDiagnostic()
    {
        return diagnostic;
    }

    public String getRecomandation()
    {
        return recomandation;
    }

    public void setCure(String cure)
    {
        this.cure = cure;
    }

    public void setDiagnostic(String diagnostic)
    {
        this.diagnostic = diagnostic;
    }

    public void setExaminationDate(String dateToConvert)
    {
        DateTimeFormatter formatter = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ofPattern(DATE_FORMAT, Locale.ENGLISH);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            examinationDate = LocalDate.parse(dateToConvert, formatter);
        }
    }

    public void setRecomandation(String recomandation)
    {
        this.recomandation = recomandation;
    }

    private LocalDate examinationDate;
    private String diagnostic;
    private String cure;
    private String recomandation;
    private final static String DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";
}