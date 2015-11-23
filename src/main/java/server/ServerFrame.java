package server;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ServerFrame extends JFrame
{
  private JTextArea allmsg;
  private JTextField currnum;
  private JTextField totalnum;
  private JTextField copyright;
  private JTextField chatmsg;
  private JButton send;
  private JScrollPane js;
  int num1;
  int num2;
  int port;
  private ServerSocket ss;
  ArrayList lists;

  public ServerFrame()
  {
    super("聊天室服务器端");
    setSize(307, 306);
    setLocationRelativeTo(null);

    this.lists = new ArrayList();

    this.num1 = (this.num2 = 0);
    this.port = 7777;

    this.currnum = new JTextField(" 当前在线人数： " + this.num1);
    this.currnum.setEnabled(false);

    this.totalnum = new JTextField(" 上线总人数： " + this.num2);
    this.totalnum.setEnabled(false);

    this.copyright = new JTextField("  -----  all copyright @ TOP-king  -----");
    this.copyright.setEnabled(false);

    this.allmsg = new JTextArea();

    this.allmsg.append("        --------------- 系统消息 --------------\n");
    this.allmsg.setEditable(false);
    this.allmsg.setLineWrap(true);

    this.js = new JScrollPane(this.allmsg);

    this.chatmsg = new JTextField("在此输入系统信息");

    this.chatmsg.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent arg0) {
        String str = ServerFrame.this.chatmsg.getText().trim();
        if (!"".equals(str)) {
          ServerFrame.this.sendmsg(new Date().toLocaleString() + " -- 系统消息： " + str); ServerFrame.this.chatmsg.setText("");
        } else {
          JOptionPane.showMessageDialog(null, "消息不能为空", "错误", 0);
        }ServerFrame.this.chatmsg.setText("");
      }
    });
    this.chatmsg.addMouseListener(new MouseListener()
    {
      public void mouseClicked(MouseEvent arg0)
      {
        ServerFrame.this.chatmsg.setText("");
      }

      public void mouseEntered(MouseEvent arg0)
      {
      }

      public void mouseExited(MouseEvent arg0)
      {
        if (ServerFrame.this.chatmsg.getText().equals(""))
          ServerFrame.this.chatmsg.setText("在此输入系统信息");
      }

      public void mousePressed(MouseEvent arg0)
      {
      }

      public void mouseReleased(MouseEvent arg0)
      {
      }
    });
    this.send = new JButton("发送");
    this.send.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent arg0) {
        String str = ServerFrame.this.chatmsg.getText().trim();
        if (!"".equals(str)) {
          ServerFrame.this.sendmsg(new Date().toLocaleString() + " -- 系统消息： " + str); ServerFrame.this.chatmsg.setText("");
        } else {
          JOptionPane.showMessageDialog(null, "消息不能为空", "错误", 0);
        }ServerFrame.this.chatmsg.setText("");
      }
    });
    addcomponettocontainer();
    addWindowListener(new WindowAdapter()
    {
      public void windowClosing(WindowEvent we) {
        ServerFrame.this.sendmsg("SYSTEM_CLOSED");
        ServerFrame.this.destory();
      }
    });
    start();
  }

  public void addcomponettocontainer()
  {
    Container c = getContentPane();
    c.setLayout(null);

    this.currnum.setBounds(10, 15, 137, 20);
    this.totalnum.setBounds(153, 15, 138, 20);
    this.js.setBounds(10, 50, 281, 162);
    this.chatmsg.setBounds(12, 218, 203, 30);
    this.send.setBounds(221, 217, 70, 30);
    this.copyright.setBounds(22, 254, 258, 20);

    c.add(this.currnum);
    c.add(this.totalnum);
    c.add(this.js);
    c.add(this.chatmsg);
    c.add(this.send);
    c.add(this.copyright);

    setVisible(true);
    setResizable(false);
  }

  public void start()
  {
    boolean isStarted = false;
    try {
      this.ss = new ServerSocket(this.port);
      isStarted = true;
      this.allmsg.append(new Date().toLocaleString() + "    服务器启动 @ 端口: " + this.port + "\n");
      while (isStarted)
      {
        Socket client = this.ss.accept();
        DataInputStream in = new DataInputStream(client.getInputStream());
        String name = in.readUTF();
        user u = new user();
        u.name = name;
        u.socket = client;
        this.lists.add(u);
        this.num1 += 1;
        this.num2 += 1;
        this.currnum.setText(" 当前在线人数： " + this.num1);
        this.totalnum.setText(" 上线总人数： " + this.num2);
        this.allmsg.append(new Date().toLocaleString() + " : " + u.name + " 登录 \n");
        new Thread(new ClientThread(u)).start();
      }
    }
    catch (IOException e) {
      System.out.println("服务器已经启动......");
      System.exit(0);
    }
  }

  public void sendmsg(String msg)
  {
    user us = new user();
    DataOutputStream os = null;
    if (this.lists.size() > 0)
    {
      for (int i = 0; i < this.lists.size(); i++)
      {
        us = (user)this.lists.get(i);
        try {
          os = new DataOutputStream(us.socket.getOutputStream());
          os.writeUTF(msg);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    else
      JOptionPane.showMessageDialog(null, "当前无用户在线。发送消息失败", "失败", 0);
  }

  public void destory()
  {
    try {
      this.ss.close();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    dispose();
  }

  public static void main(String[] args)
  {
    new ServerFrame();
  }

  class ClientThread
    implements Runnable
  {
    user user = null;
    boolean isConnected = true;
    DataInputStream dis = null;
    DataOutputStream dos = null;

    public ClientThread(user u)
    {
      this.user = u;
      try {
        this.dis = new DataInputStream(this.user.socket.getInputStream());
        this.dos = new DataOutputStream(this.user.socket.getOutputStream());
      }
      catch (IOException e) {
        e.printStackTrace();
      }
    }

    public void run()
    {
      readmsg();
    }

    public void readmsg()
    {
      while (this.isConnected)
        try
        {
          String msg = this.dis.readUTF();
          if ("quit&logout".equals(msg))
          {
            ServerFrame.this.num1 -= 1;
            try {
              this.dis.close();
              this.dos.close();
              this.user.socket.close();
              this.isConnected = false;
            }
            catch (IOException ioe) {
              ioe.printStackTrace();
            } finally {
              this.isConnected = false;
              if (this.dis != null) this.dis.close();
              if (this.dos != null) this.dos.close();
              if (this.user.socket != null) this.user.socket.close();
//              ret;
            }
            ServerFrame.this.currnum.setText(" 当前在线人数： " + ServerFrame.this.num1);
            ServerFrame.this.allmsg.append(new Date().toLocaleString() + "  : " + this.user.name + "  退出\n");
          }
          else {
            ServerFrame.this.sendmsg(msg);
          }
        } catch (IOException e) { e.printStackTrace(); }

    }
  }
}