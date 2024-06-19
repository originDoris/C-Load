package com.cl.framework.core.web.core.di;

import com.cl.framework.core.enums.ErrorCodeEnum;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 依赖图工具 ，判断是否存在循环依赖问题
 *
 * @author xhz
 */
public class DependencyGraph {

    private static ConcurrentHashMap<String, List<String>> GRAPH = new ConcurrentHashMap<>();

    public static void addDependency(String source, String target) {
        GRAPH.computeIfAbsent(source, k -> new CopyOnWriteArrayList<>()).add(target);
    }

    public static void hasCycle() {
        ConcurrentHashMap<String, AtomicInteger> inDegree = new ConcurrentHashMap<>();
        Set<String> allNodes = new ConcurrentSkipListSet<>();

        GRAPH.forEach((source, targets) -> {
            inDegree.computeIfAbsent(source, k -> new AtomicInteger(0));
            allNodes.add(source);
            targets.forEach(target -> {
                allNodes.add(target);
                inDegree.computeIfAbsent(target, k -> new AtomicInteger(0)).incrementAndGet();
            });
        });

        Queue<String> queue = new LinkedList<>();
        allNodes.forEach(node -> {
            if (inDegree.get(node).get() == 0) {
                queue.offer(node);
            }
        });

        int count = 0;
        while (!queue.isEmpty()) {
            String current = queue.poll();
            count++;
            List<String> neighbors = GRAPH.get(current);
            if (neighbors != null) {
                for (String neighbor : neighbors) {
                    if (inDegree.get(neighbor).decrementAndGet() == 0) {
                        queue.offer(neighbor);
                    }
                }
            }
        }

        if (count != inDegree.size()) {
            if (inDegree.isEmpty()) {
                return;
            }
            StringBuilder error = new StringBuilder("存在循环依赖，无法实现依赖注入，请检查以下类中是否存在循环依赖：");
            for (String className : inDegree.keySet()) {
                error.append(className).append(", ");
            }
            error.deleteCharAt(error.length() - 2);


            throw new com.cl.framework.core.exception.DataTableException(ErrorCodeEnum.CYCLIC_DEPENDENCE_ERROR, error.toString());
        }

    }

}
