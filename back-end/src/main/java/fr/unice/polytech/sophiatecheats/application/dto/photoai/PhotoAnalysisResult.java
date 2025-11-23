package fr.unice.polytech.sophiatecheats.application.dto.photoai;

import java.util.List;

public class PhotoAnalysisResult {
    private final String description;
    private final String category;
    private final List<String> tags;

    public PhotoAnalysisResult(String description, String category, List<String> tags) {
        this.description = description;
        this.category = category;
        this.tags = tags;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public List<String> getTags() {
        return tags;
    }
}
