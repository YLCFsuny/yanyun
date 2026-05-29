import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class FSTNode {
    private final Map<Character, FSTNode> transitions = new HashMap<>();
    // Getter和Setter方法
    @Setter
    private boolean isFinal;
    @Setter
    private String output;

    public FSTNode() {
        this.isFinal = false;
        this.output = null;
    }

    // 添加状态转移
    public void addTransition(char c, FSTNode nextNode) {
        transitions.put(c, nextNode);
    }

    // 获取下一个状态节点
    public FSTNode getNextNode(char c) {
        return transitions.get(c);
    }
}
