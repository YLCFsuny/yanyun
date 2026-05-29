import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FST {
    private final FSTNode root;

    public FST() {
        this.root = new FSTNode();
    }

    // 插入键值对
    public void insert(String key, String value) {
        FSTNode current = root;

        for (char c : key.toCharArray()) {
            FSTNode nextNode = current.getNextNode(c);
            if (nextNode == null) {
                nextNode = new FSTNode();
                current.addTransition(c, nextNode);
            }
            current = nextNode;
        }

        current.setFinal(true);
        current.setOutput(value);
    }

    // 查找键对应的值
    public String search(String key) {
        FSTNode current = root;

        for (char c : key.toCharArray()) {
            current = current.getNextNode(c);
            if (current == null) {
                return null; // 未找到
            }
        }

        return current.isFinal() ? current.getOutput() : null;  // 找到但不是最终节点
    }

    // 前缀查询（返回所有以指定前缀开头的键）
    public List<String> prefixSearch(String prefix) {
        List<String> results = new ArrayList<>();
        FSTNode current = root;

        // 导航到前缀的最后一个节点
        for (char c : prefix.toCharArray()) {
            current = current.getNextNode(c);
            if (current == null) {
                return results; // 空列表
            }
        }

        // 收集所有以该前缀结尾的键
        collectKeys(current, prefix, results);
        return results;
    }

    private void collectKeys(FSTNode node, String currentPrefix, List<String> results) {
        if (node.isFinal()) {
            results.add(currentPrefix);
        }

        for (Map.Entry<Character, FSTNode> entry : node.getTransitions().entrySet()) {
            collectKeys(entry.getValue(), currentPrefix + entry.getKey(), results);
        }
    }
}

