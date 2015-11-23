package client;

import java.awt.Color;
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
import java.net.Socket;
import java.util.Date;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ClientFrame extends JFrame
{
  private JTextArea allmsg;
  private JTextField welcome;
  private JTextField chatmsg;
  private JButton send;
  private JScrollPane js;
  private boolean isConnected = true;
  private int count = 1;
  public DataOutputStream out;
  public DataInputStream in;
  public Socket s = null;
  String nic;

  public ClientFrame(String name, Socket socket)
  {
    setSize(402, 418);
    setLocationRelativeTo(null);
    setTitle("聊天室客户端<" + name + ">");

    this.s = socket;

    this.nic = (name + " 说： ");

    this.welcome = new JTextField(" < " + name + " >欢迎您来到聊天室 ", 100);
    this.welcome.setBackground(Color.blue);
    this.welcome.setEnabled(false);

    this.allmsg = new JTextArea();
    this.allmsg.setEditable(false);
    this.allmsg.append("                  系统消息: 欢迎登录在线聊天室 \n");

    this.js = new JScrollPane(this.allmsg);

    this.chatmsg = new JTextField("在此输入聊天信息");
    this.chatmsg.addActionListener(new listen());
    this.chatmsg.addMouseListener(new MouseListener()
    {
      public void mouseClicked(MouseEvent e)
      {
        if (ClientFrame.this.count == 1)
        {
          ClientFrame.this.chatmsg.setText("");
          ClientFrame.this.count += 1;
        }
      }

      public void mouseEntered(MouseEvent arg0)
      {
      }

      public void mouseExited(MouseEvent arg0)
      {
      }

      public void mousePressed(MouseEvent arg0)
      {
      }

      public void mouseReleased(MouseEvent arg0)
      {
      }
    });
    this.send = new JButton("发送");
    this.send.addActionListener(new listen());
    try
    {
      this.out = new DataOutputStream(this.s.getOutputStream());
      this.in = new DataInputStream(this.s.getInputStream()); } catch (IOException e) {
      JOptionPane.showMessageDialog(null, "系统异常", "错误", 2);
    }
    addcomponettocontainer();

    addWindowListener(new WindowAdapter()
    {
      public void windowClosing(WindowEvent we) {
        ClientFrame.this.sendmsg("quit&logout");
        ClientFrame.this.isConnected = false;
        ClientFrame.this.destory();
      }
    });
    new Thread(new linread()).start();
  }

  public void addcomponettocontainer()
  {
    Container c = getContentPane();
    c.setLayout(null);

    this.welcome.setBounds(101, 10, 189, 20);
    this.js.setBounds(10, 43, 376, 254);
    this.chatmsg.setBounds(10, 303, 376, 30);
    this.send.setBounds(316, 351, 70, 30);

    c.add(this.welcome);
    c.add(this.js);
    c.add(this.chatmsg);
    c.add(this.send);

    setVisible(true);
    setResizable(false);
  }

  public void sendmsg(String m)
  {
    if (this.isConnected) {
      try
      {
        this.out.writeUTF(m);
      } catch (IOException e) {
        JOptionPane.showMessageDialog(null, "发送信息失败!(系统异常)", "错误", 0);
      }
    }
    else
    {
      JOptionPane.showMessageDialog(null, "发送信息失败!(服务器关闭/网络故障)", "错误", 0);
    }
  }

  public void destory()
  {
    try
    {
      this.out.close();
      this.in.close();
      this.s.close();
    } catch (IOException localIOException) {
    }
    dispose();
  }

  class listen
    implements ActionListener
  {
    listen()
    {
    }

    public void actionPerformed(ActionEvent e)
    {
      if ((e.getSource() == ClientFrame.this.send) || (e.getSource() == ClientFrame.this.chatmsg))
      {
        String msg = ClientFrame.this.chatmsg.getText().trim();
        if ("".equals(msg))
        {
          JOptionPane.showMessageDialog(null, "发送信息不能为空!", "错误", 0);
        }
        else {
          ClientFrame.this.sendmsg(new Date().toLocaleString() + "\n" + ClientFrame.this.nic + msg + "\n"); ClientFrame.this.chatmsg.setText("");
        }
      }
    }
  }

  class linread
    implements Runnable
  {
    linread()
    {
    }

    public void run()
    {
      read();
    }

    public void read()
    {
      while (ClientFrame.this.isConnected)
        try
        {
          String msg = ClientFrame.this.in.readUTF();
          if ("SYSTEM_CLOSED".equals(msg))
          {
            JOptionPane.showMessageDialog(null, "读取消息失败(服务器关闭/网络故障)！", "错误", 0);
            ClientFrame.this.isConnected = false;
          }
          else {
            ClientFrame.this.allmsg.append(msg + "\n");
          }
        } catch (IOException localIOException) {
        }
      JOptionPane.showMessageDialog(null, "读取消息失败(服务器关闭/网络故障)！", "错误", 0);
    }
  }
}