package com.vn.EduQuest.payload.response.exercise;

import java.math.BigDecimal;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class ExerciseScoreExport {
    String name;          
    String studentCode;    
    String className;       
    String exerciseName;    
    BigDecimal score;     
}