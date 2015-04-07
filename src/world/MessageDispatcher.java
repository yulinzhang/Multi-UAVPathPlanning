/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package world;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 *
 * @author Yulin_Zhang
 */
public interface MessageDispatcher {

    public Map<Integer, LinkedList<Message>> recv_msg_list = new HashMap<Integer, LinkedList<Message>>();

    public void dispatch();

    public void getNumOfMsgLatestSent();

    public void getTotalNumOfMsgSent();

    public static void clearRecvMsgList()
    {
        MessageDispatcher.recv_msg_list.clear();
    }
}

