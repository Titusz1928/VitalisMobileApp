package com.example.ip_mobileapp.Model;

public class SensorSettings
{
    public SamplingPeriod getSamplingPeriod()
    {
        return samplingPeriod;
    }

    public SensorsReferences getSensorsReferences()
    {
        return sensorsReferences;
    }

    public void setSamplingPeriod(SamplingPeriod samplingPeriod)
    {
        this.samplingPeriod = samplingPeriod;
    }

    public void setSensorsReferences(SensorsReferences sensorsReferences)
    {
        this.sensorsReferences = sensorsReferences;
    }

    private SensorsReferences sensorsReferences;
    private SamplingPeriod samplingPeriod;
}
