/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package world;

/**
 *
 * @author Yulin_Zhang
 */
public abstract class Message {
    public int msg_id=-1;
    public int msg_type=-1;
    public String content=null;
    
    public static int CONFLICT_MSG=1;
    public static int OBSTACLE_MSG=2;
    public static int THREAT_MSG=3;

    public abstract int getMsgSize();
    
    public int getMsg_type() {
        return msg_type;
    }

    public void setMsg_type(int msg_type) {
        this.msg_type = msg_type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getMsg_id() {
        return msg_id;
    }

    public void setMsg_id(int msg_id) {
        this.msg_id = msg_id;
    }
    
    
}
