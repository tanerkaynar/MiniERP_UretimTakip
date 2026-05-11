using System;
using System.Windows.Forms;

namespace MiniERP_UretimTakip
{
    internal static class Program
    {
        [STAThread]
        private static void Main()
        {
            Application.EnableVisualStyles();
            Application.SetCompatibleTextRenderingDefault(false);
            /*Application.Run(new FrmAnaMenu());*/
            Application.Run(new SevkiyatForm());
        }
    }
}
