package fr.unice.polytech.sophiatecheats.application.dto.photoai;

import java.util.List;

public record PhotoAnalysisResult(String description, String category, List<String> tags) {
}
