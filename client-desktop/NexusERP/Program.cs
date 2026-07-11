using System;
using System.Windows.Forms;

namespace NexusERP
{
    internal static class Program
    {
        [STAThread]
        private static void Main()
        {
            Application.EnableVisualStyles();
            Application.SetCompatibleTextRenderingDefault(false);

            while (true)
            {
                string kullaniciAdi;
                string kullaniciRol;

                using (var loginForm = new LoginForm())
                {
                    if (loginForm.ShowDialog() != DialogResult.OK)
                        return;

                    kullaniciAdi = loginForm.KullaniciAdi;
                    kullaniciRol = loginForm.KullaniciRol;
                }

                using (var anaMenu = new FrmAnaMenu(kullaniciAdi, kullaniciRol))
                {
                    Application.Run(anaMenu);

                    if (!anaMenu.OturumKapatildi)
                        break;
                }
            }
        }
    }
}