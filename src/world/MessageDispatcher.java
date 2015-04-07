/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import world.model.Obstacle;
import world.model.Target;
import world.model.WorldKnowledge;
import world.uav.UAV;

/**
 *
 * @author Yulin_Zhang
 */
public abstract class MessageDispatcher {

    protected Map<Integer, LinkedList<Message>> recv_msg_list = new HashMap<Integer, LinkedList<Message>>();
    protected int num_of_msg_sent_this_time_step = 0;
    protected int num_of_msg_sent_total = 0;
    protected World world;

    public MessageDispatcher(World world) {
        this.world = world;
    }
    public abstract void register(Integer uav_index, float[] current_loc,Target target);
    public abstract void decideAndSumitMsgToSend();

    public void dispatch() {
        List<UAV> attackers = world.getAttackers();
        int attacker_num = attackers.size();
        for (int i = 0; i < attacker_num; i++) {
            UAV attacker = attackers.get(i);
            Integer attacker_index = attacker.getIndex();
            LinkedList<Message> recv_list = recv_msg_list.get(attacker_index);
            if (recv_list != null && recv_list.size() > 0) {
                int total_msg_sent = recv_list.size();
                for (int j = 0; j < total_msg_sent; j++) {
                    Message msg=recv_list.get(j);
                    int msg_size=msg.getMsgSize();
                    attacker.receiveMesage(msg);
                    this.num_of_msg_sent_total += msg_size;
                    this.num_of_msg_sent_this_time_step +=  msg_size;
                }

            }
        }
        this.clearRecvMsgList();
    }

    public int getNumOfMsgLatestSent() {
        return this.num_of_msg_sent_this_time_step;
    }

    public int getTotalNumOfMsgSent() {
        return this.num_of_msg_sent_total;
    }

    public void clearRecvMsgList() {
        this.recv_msg_list.clear();
    }

    public void addRecvMessage(Integer recv_uav_index, Message msg) {
        LinkedList<Message> recv_list = this.recv_msg_list.get(recv_uav_index);
        if (recv_list == null) {
            recv_list = new LinkedList<Message>();
        }
        recv_list.add(msg);
        this.recv_msg_list.put(recv_uav_index, recv_list);
    }
}
