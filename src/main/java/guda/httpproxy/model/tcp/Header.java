package guda.httpproxy.model.tcp;

/**
 * Created by well on 2014/12/8.
 */
public class Header {

    /**
     * 总报文长度
     */
    private Integer totalLength;
    /**
     * 头部长度
     */
    private Short headerLegth ;
    /**
     * messageid
     */
    private Integer messageId;

    /**
     * 协议版本
     */
    private  Short protocolVersion ;

    /**
     * 消息类型
     */
    private Short messageType;

    /**
     * 加密类型
     */
    private Byte encryptType;

    /**
     * 可靠性级别
     */
    private  Byte qosLevel;
    /**
     * 消息关联级别
     */
    private Byte actionType;

    public Integer getTotalLength() {
        return totalLength;
    }

    public void setTotalLength(Integer totalLength) {
        this.totalLength = totalLength;
    }

    public Short getHeaderLegth() {
        return headerLegth;
    }

    public void setHeaderLegth(Short headerLegth) {
        this.headerLegth = headerLegth;
    }

    public Integer getMessageId() {
        return messageId;
    }

    public void setMessageId(Integer messageId) {
        this.messageId = messageId;
    }

    public Short getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(Short protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public Short getMessageType() {
        return messageType;
    }

    public void setMessageType(Short messageType) {
        this.messageType = messageType;
    }

    public Byte getEncryptType() {
        return encryptType;
    }

    public void setEncryptType(Byte encryptType) {
        this.encryptType = encryptType;
    }

    public Byte getQosLevel() {
        return qosLevel;
    }

    public void setQosLevel(Byte qosLevel) {
        this.qosLevel = qosLevel;
    }

    public Byte getActionType() {
        return actionType;
    }

    public void setActionType(Byte actionType) {
        this.actionType = actionType;
    }

    /**
     * 复制新对象
     *
     * @return
     */
    public Header copy() {
        Header h = new Header();
        h.setActionType(actionType);
        h.setEncryptType(encryptType);
        h.setHeaderLegth(headerLegth);
        h.setMessageId(messageId);
        h.setMessageType(messageType);
        h.setProtocolVersion(protocolVersion);
        h.setQosLevel(qosLevel);
        h.setTotalLength(totalLength);
        return h;
    }

    @Override
    public String toString() {
        return "Header [totalLength=" + totalLength + ", headerLegth=" + headerLegth + ", messageId=" + messageId
                + ", protocolVersion=" + protocolVersion + ", messageType=" + messageType + ", encryptType="
                + encryptType + ", qosLevel=" + qosLevel + ", actionType=" + actionType + "]";
    }
}
