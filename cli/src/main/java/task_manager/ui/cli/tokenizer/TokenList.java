package task_manager.ui.cli.tokenizer;

import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Set;

public record TokenList(List<String> tokens, Set<Pair<Integer, Integer>> escapedPositions) { }
