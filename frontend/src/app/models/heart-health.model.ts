export interface HeartHealthRecordRequest {
  bloodPressureSystolic: number;
  bloodPressureDiastolic: number;
  heartRate: number;
  oxygenSaturation: number;
  bodyWeight?: number;
  symptoms?: string[];
  recordedAt: string;
}

export interface HeartHealthRecord {
  id: number;
  userId: number;
  bloodPressureSystolic: number;
  bloodPressureDiastolic: number;
  heartRate: number;
  oxygenSaturation: number;
  bodyWeight?: number;
  symptoms?: string[];
  recordedAt: string;
  createdAt: string;
}

export interface HeartHealthRecordList {
  items: HeartHealthRecord[];
}

export interface HeartHealthReport {
  period: {
    startDate: string;
    endDate: string;
  };
  averageBloodPressure: {
    systolic: number;
    diastolic: number;
  };
  averageHeartRate: number;
  averageOxygenSaturation: number;
  weightVariation: number;
  symptomOccurrences: { [key: string]: number };
  riskLevel: string;
}
