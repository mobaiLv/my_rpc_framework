package github.mobai.curator;

import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author mobai
 * @date 2022/3/1
 */
public class CuratorFrameworkDemo {

    private static final int BASE_SLEEP_TIME = 1000;
    private static final int MAX_RETRIES = 3;

    public static void main(String[] args) throws Exception {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(BASE_SLEEP_TIME, MAX_RETRIES);
        CuratorFramework zkClient = CuratorFrameworkFactory.builder()
                .connectString("127.0.0.1:2181")
                .retryPolicy(retryPolicy)
                .build();
        zkClient.start();

        //createPersistentNode(zkClient);
        //createEphemeralNode(zkClient);
        //createNodeAndSetData(zkClient);
        //checkNodeExists(zkClient);
        //deleteLeafNode(zkClient);
        //deleteNodeAndChildNodes(zkClient);
        //getAndSetNodeData(zkClient);
        //getAllChildren(zkClient);
        watchChildrenNodes(zkClient);
    }

    // 创建持久节点, creatingParentsIfNeeded保证不存在父节点时自动创建
    public static void createPersistentNode(CuratorFramework zkClient) throws Exception {
        zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath("/node1/00001");
    }

    // 创建临时节点
    public static void createEphemeralNode(CuratorFramework zkClient) throws Exception {
        zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath("/node1/00001");
    }

    // 创建节点的同时设置节点数据
    public static void createNodeAndSetData(CuratorFramework zkClient) throws Exception {
        zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath("/node1/00001", "java".getBytes(StandardCharsets.UTF_8));
        String data = new String(zkClient.getData().forPath("/node1/00001"));
        System.out.println(data);
    }

    // 检查节点是否存在
    public static void checkNodeExists(CuratorFramework zkClient) throws Exception {
        Stat stat = zkClient.checkExists().forPath("/node1/00001");
        if (null == stat) {
            System.out.println("节点不存在");
        } else {
            System.out.println("节点存在");
        }
    }

    // 删除一个叶子节点
    public static void deleteLeafNode(CuratorFramework zkClient) throws Exception {
        zkClient.delete().forPath("/node1/00001");
    }

    // 删除一个节点以及所有子节点
    public static void deleteNodeAndChildNodes(CuratorFramework zkClient) throws Exception {
        zkClient.delete().deletingChildrenIfNeeded().forPath("/node1");
    }

    // 获取和设置节点数据
    public static void getAndSetNodeData(CuratorFramework zkClient) throws Exception {
        zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath("/node1/node1.1", "java".getBytes());
        System.out.println(new String(zkClient.getData().forPath("/node1/node1.1")));
        zkClient.setData().forPath("/node1/node1.1", "c++".getBytes());
    }

    public static void getAllChildren(CuratorFramework zkClient) throws Exception {
        List<String> childrens = zkClient.getChildren().forPath("/node1");
        System.out.println(childrens);
    }

    public static void watchChildrenNodes(CuratorFramework zkClient) throws Exception {
        String path = "/node1";
        PathChildrenCache pathChildrenCache = new PathChildrenCache(zkClient, path, true);

        pathChildrenCache.getListenable().addListener((curatorFramework, pathChildrenCacheEvent) -> {
            if (pathChildrenCacheEvent.getType().equals(PathChildrenCacheEvent.Type.CHILD_ADDED)) {
                System.out.println(path + " 增加了一个子节点 path:" + pathChildrenCacheEvent.getData().getPath() + " data:" + new String(pathChildrenCacheEvent.getData().getData()));
            } else if (pathChildrenCacheEvent.getType().equals(PathChildrenCacheEvent.Type.CHILD_REMOVED)){
                System.out.println(path + "删除了一个子节点 path:" + pathChildrenCacheEvent.getData().getPath());
            } else if (pathChildrenCacheEvent.getType().equals(PathChildrenCacheEvent.Type.CHILD_UPDATED)) {
                System.out.println(path + " 一个子节点数据更新 path:" + pathChildrenCacheEvent.getData().getPath() + " data:" + new String(pathChildrenCacheEvent.getData().getData()));
            } else {

            }
        });
        pathChildrenCache.start();
        System.in.read();
    }
}
