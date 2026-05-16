package com.stock.analysis.dto;

import lombok.Data;
import java.util.List;

@Data
public class StrategyTemplateVO {
    private String type;
    private String name;
    private String description;
    private List<ParamDef> parameters;

    @Data
    public static class ParamDef {
        private String name;
        private String label;
        private String type;
        private Object defaultValue;
    }
}
