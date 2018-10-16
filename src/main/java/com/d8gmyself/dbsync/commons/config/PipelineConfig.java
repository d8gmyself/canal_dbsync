package com.d8gmyself.dbsync.commons.config;

import com.d8gmyself.dbsync.commons.model.ColumnPair;
import com.d8gmyself.dbsync.commons.model.DataMediaPair;
import com.d8gmyself.dbsync.commons.model.Pipeline;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Created by ZhangDuo on 2016-3-10 13:23.
 * <p>
 * 读取mapping.xml的辅助类
 *
 * @author ZhangDuo
 */
public class PipelineConfig {

    private static final Logger log = LoggerFactory.getLogger(PipelineConfig.class);

    /**
     * 映射文件名
     */
    private static final String xmlFileName = "pipeline.xml";

    private static Document document;

    static {
        try {
            document = new SAXReader().read(PipelineConfig.class.getClassLoader().getResourceAsStream(xmlFileName));
            log.debug(xmlFileName + " loaded");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private PipelineConfig() {

    }

    /**
     * 通过pipelineId获取其表同步映射关系
     *
     * @param pipelineId pipelineId
     * @return 表同步映射关系
     */
    public static Map<String, List<DataMediaPair>> getDataMediaPairs(Long pipelineId) {
        Map<String, List<DataMediaPair>> dataMediaPairs = Maps.newHashMap();
        List<Node> pipelineTables = document.selectNodes("/mapping/pipeline[@id='" + pipelineId + "']/table");
        if (CollectionUtils.isNotEmpty(pipelineTables)) {
            for (Node tableNode : pipelineTables) {
                DataMediaPair dataMediaPair = new DataMediaPair();
                dataMediaPair.setPipelineId(pipelineId);
                dataMediaPair.setId(Integer.valueOf(tableNode.selectSingleNode("@id").getText()));
                String src = tableNode.selectSingleNode("src").getText();
                String target = tableNode.selectSingleNode("target").getText();
                dataMediaPair.setSrcSchema(src.split("\\.")[0]);
                dataMediaPair.setSrcTableName(src.split("\\.")[1]);
                dataMediaPair.setTargetSchema(target.split("\\.")[0]);
                dataMediaPair.setTargetTableName(target.split("\\.")[1]);
                dataMediaPair.setColumnPairs(Maps.<String, ColumnPair>newHashMap());
                Node fieldsNode = tableNode.selectSingleNode("fields");
                StringBuilder key = new StringBuilder(dataMediaPair.getSrcSchema()).append(".").append(dataMediaPair.getSrcTableName());
                if (fieldsNode != null) {
                    for (Node fieldNode : fieldsNode.selectNodes("field")) {
                        Node srcColumnNameNode = fieldNode.selectSingleNode("@src");
                        Node targetColumnNameNode = fieldNode.selectSingleNode("@target");
                        Node defaultValueNode = fieldNode.selectSingleNode("@defalultValue");
                        ColumnPair columnPair = new ColumnPair();
                        columnPair.setSrcName(srcColumnNameNode.getText());
                        columnPair.setTargetName(targetColumnNameNode == null ? columnPair.getSrcName() : targetColumnNameNode.getText());
                        columnPair.setDefaultValue(defaultValueNode == null ? null : defaultValueNode.getText());
                        dataMediaPair.getColumnPairs().put(columnPair.getSrcName(), columnPair);
                    }
                }
                if (dataMediaPairs.get(key.toString()) != null) {
                    dataMediaPairs.get(key.toString()).add(dataMediaPair);
                } else {
                    dataMediaPairs.put(key.toString(), Lists.newArrayList(dataMediaPair));
                }
            }
        }
        return dataMediaPairs;
    }

    /**
     * 获取所有pipeline配置
     *
     * @return pipeline配置
     */
    public static Map<Long, Pipeline> getPipelines() {
        Map<Long, Pipeline> pipelines = Maps.newHashMap();
        List<Node> pipelineNodes = document.selectNodes("/mapping/pipeline");
        for (Node pipelineNode : pipelineNodes) {
            Pipeline pipeline = new Pipeline();
            pipeline.setId(Long.valueOf(pipelineNode.selectSingleNode("@id").getText()));
            pipeline.setZkServers(pipelineNode.selectSingleNode("@zkServers").getText());
            pipeline.setDestination(pipelineNode.selectSingleNode("@destination").getText());
            pipeline.setCanalUsername(pipelineNode.selectSingleNode("@canalUsername").getText());
            pipeline.setCanalPassword(pipelineNode.selectSingleNode("@canalPassword").getText());
            pipeline.setParallelism(Integer.valueOf(pipelineNode.selectSingleNode("@parallelism").getText()));
            pipeline.setBatchSize(Integer.valueOf(pipelineNode.selectSingleNode("@batchSize").getText()));
            pipeline.setDataMediaPairs(getDataMediaPairs(pipeline.getId()));
            pipelines.put(pipeline.getId(), pipeline);
        }
        return pipelines;
    }

    /**
     * 获取指定id的pipeline配置
     *
     * @param pipelineId pipelineId
     * @return pipeline配置
     */
    public static Pipeline getPipelineById(Long pipelineId) {
        Preconditions.checkNotNull(pipelineId, "pipelineId can not be null...");
        Node pipelineNode = document.selectSingleNode("/mapping/pipeline[@id='" + pipelineId + "']");
        if (pipelineNode != null) {
            Pipeline pipeline = new Pipeline();
            pipeline.setId(Long.valueOf(pipelineNode.selectSingleNode("@id").getText()));
            pipeline.setZkServers(pipelineNode.selectSingleNode("@zkServers").getText());
            pipeline.setDestination(pipelineNode.selectSingleNode("@destination").getText());
            pipeline.setCanalUsername(pipelineNode.selectSingleNode("@canalUsername").getText());
            pipeline.setCanalPassword(pipelineNode.selectSingleNode("@canalPassword").getText());
            pipeline.setParallelism(Integer.valueOf(pipelineNode.selectSingleNode("@parallelism").getText()));
            pipeline.setBatchSize(Integer.valueOf(pipelineNode.selectSingleNode("@batchSize").getText()));
            pipeline.setDataMediaPairs(getDataMediaPairs(pipeline.getId()));
            return pipeline;
        } else {
            return null;
        }
    }

}
