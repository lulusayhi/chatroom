package client;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class LoginFrame extends JFrame
  implements MouseListener
{
  private JPasswordField password;
  private static final long serialVersionUID = -6064490029607774863L;
  private JTextField name;
  private JTextField ip;
  private JButton ok;
  private JButton cancle;
  final JLabel top = new JLabel();
  final JLabel tishi = new JLabel();
  public Socket socket;

  public LoginFrame()
  {
    super("登录框");
    setSize(350, 287);
    setLocationRelativeTo(null);

    this.name = new JTextField("输入昵称");
    this.ip = new JTextField("127.0.0.1");
    this.ok = new JButton("登录");
    this.cancle = new JButton("取消");
    this.ok.addActionListener(new listenEvent());
    this.cancle.addActionListener(new listenEvent());
    addcomponettocontainer();
    setDefaultCloseOperation(3);

    this.name.addMouseListener(this);
    this.ip.addMouseListener(this);

    this.top.setBounds(0, 0, 344, 100);
    URL IconUrl = LoginFrame.class.getResource("/top.jpg");
    ImageIcon icon = new ImageIcon(IconUrl);
    this.top.setIcon(icon);
    getContentPane().add(this.top);

    JLabel label = new JLabel();
    label.setText("用户名：");
    label.setBounds(45, 118, 62, 23);
    getContentPane().add(label);

    JLabel label_1 = new JLabel();
    label_1.setText("密  码：");
    label_1.setBounds(45, 157, 62, 23);
    getContentPane().add(label_1);

    this.password = new JPasswordField();
    this.password.setFont(new Font("", 1, 16));
    this.password.setText("admin");
    this.password.setBounds(113, 157, 118, 24);
    getContentPane().add(this.password);
    this.password.addMouseListener(this);
    this.password.addActionListener(new listenEvent());

    this.tishi.setBounds(94, 235, 130, 20);
    this.tishi.setForeground(new Color(255, 0, 128));
    this.tishi.setFont(new Font("", 3, 14));
    getContentPane().add(this.tishi);

    JLabel label_2 = new JLabel();
    label_2.setText("IP地址：");
    label_2.setBounds(47, 200, 60, 20);
    getContentPane().add(label_2);

    setVisible(true);
  }

  public void addcomponettocontainer()
  {
    Container c = getContentPane();
    c.setLayout(null);

    this.name.setBounds(113, 118, 118, 24);
    this.ip.setBounds(113, 200, 118, 24);
    this.ok.setBounds(264, 144, 70, 30);
    this.cancle.setBounds(264, 196, 70, 30);

    c.add(this.name);
    c.add(this.ip);
    c.add(this.ok);
    c.add(this.cancle);

    setResizable(false);
  }

  public void login(String name, String ip)
  {
    try
    {
      this.socket = new Socket(ip, 7777);
      DataOutputStream out = new DataOutputStream(this.socket.getOutputStream());
      out.writeUTF(name);
      out.flush();

      new ClientFrame(name, this.socket);
      destroywindow();
    } catch (UnknownHostException e) {
      JOptionPane.showMessageDialog(null, "找不到主机地址(IP错误/网络故障)！", "错误", 0);
    }
    catch (IOException localIOException)
    {
    }
  }

  public void destroywindow()
  {
    dispose();
  }

  public void mouseClicked(MouseEvent e)
  {
    if (e.getSource() == this.name)
      this.name.setText("");
    if (e.getSource() == this.password)
    {
      this.password.setText("");
      this.tishi.setText("");
    }
    if (e.getSource() == this.ip)
      this.ip.setText("");
  }

  public void mouseEntered(MouseEvent e)
  {
  }

  public void mouseExited(MouseEvent e)
  {
    if (this.name.getText().equals(""))
      this.name.setText("输入昵称");
    if (this.password.getText().equals(""))
      this.password.setText("admin");
    if (this.ip.getText().equals(""))
      this.ip.setText("127.0.0.1");
  }

  public void mousePressed(MouseEvent e)
  {
  }

  public void mouseReleased(MouseEvent arg0)
  {
  }

  public static void main(String[] args)
  {
    new LoginFrame();
  }

  public class listenEvent
    implements ActionListener
  {
    public listenEvent()
    {
    }

    public void actionPerformed(ActionEvent event)
    {
      if (event.getSource() == LoginFrame.this.ok)
      {
        String n = LoginFrame.this.name.getText().trim();
        String i = LoginFrame.this.ip.getText().trim();
        if (("".equals(n)) || ("".equals(i)))
        {
          JOptionPane.showMessageDialog(null, "昵称、IP不能够为空!", "错误", 0);
        }
        else if (LoginFrame.this.password.getText().equals("admin"))
          LoginFrame.this.tishi.setText("密码错误");
        LoginFrame.this.login(n, i);
      }
      if (event.getSource() == LoginFrame.this.cancle)
      {
        LoginFrame.this.name.setText("");
        LoginFrame.this.ip.setText("");
      }
    }
  }
}