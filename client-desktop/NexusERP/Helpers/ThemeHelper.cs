using System;
using System.Drawing;
using System.Windows.Forms;
using System.IO;

namespace NexusERP.Helpers
{
    public enum ThemeMode
    {
        Light,
        Dark
    }

    public static class ThemeHelper
    {
        private static ThemeMode currentTheme = ThemeMode.Light;

        public static ThemeMode CurrentTheme
        {
            get => currentTheme;
            set => currentTheme = value;
        }

        public static Icon GetAppIcon()
        {
            try
            {
                string path = Path.Combine(AppDomain.CurrentDomain.BaseDirectory, "app_icon.ico");
                if (File.Exists(path))
                {
                    return new Icon(path);
                }
            }
            catch { }
            return null;
        }

        public static void ApplyTheme(Form form)
        {
            form.Icon = GetAppIcon();

            if (!string.IsNullOrWhiteSpace(form.Text) && !form.Text.StartsWith("Nexus ERP"))
            {
                if (form.Text.IndexOf("NexusERP", StringComparison.OrdinalIgnoreCase) >= 0)
                {
                    form.Text = form.Text.Replace("NexusERP", "Nexus ERP");
                }
                else if (form.Text.IndexOf("Mini ERP", StringComparison.OrdinalIgnoreCase) >= 0)
                {
                    form.Text = form.Text.Replace("Mini ERP", "Nexus ERP");
                }
                else
                {
                    form.Text = "Nexus ERP - " + form.Text;
                }
            }

            if (currentTheme == ThemeMode.Dark)
            {
                form.BackColor = Color.FromArgb(17, 24, 39); 
                form.ForeColor = Color.FromArgb(243, 244, 246); 
            }
            else
            {
                form.BackColor = Color.FromArgb(244, 247, 251); 
                form.ForeColor = Color.FromArgb(17, 24, 39); 
            }

            ApplyControlTheme(form.Controls);
        }

        private static void ApplyControlTheme(Control.ControlCollection controls)
        {
            foreach (Control control in controls)
            {
                if (currentTheme == ThemeMode.Dark)
                {
                    
                    if (control is Panel panel)
                    {
                        if (panel.BackColor == Color.White || panel.BackColor == Color.FromArgb(244, 247, 251) || panel.BackColor == Color.FromArgb(235, 241, 248) || panel.BackColor == SystemColors.Control)
                        {
                            panel.BackColor = Color.FromArgb(31, 41, 55); 
                            panel.ForeColor = Color.FromArgb(243, 244, 246);
                        }
                    }
                    else if (control is Button btn)
                    {
                        if (btn.BackColor == Color.White || btn.BackColor == SystemColors.Control)
                        {
                            btn.BackColor = Color.FromArgb(55, 65, 81); 
                            btn.ForeColor = Color.White;
                            btn.FlatAppearance.BorderColor = Color.FromArgb(75, 85, 99);
                        }
                    }
                    else if (control is TextBox txt)
                    {
                        txt.BackColor = Color.FromArgb(55, 65, 81);
                        txt.ForeColor = Color.White;
                        txt.BorderStyle = BorderStyle.FixedSingle;
                    }
                    else if (control is ComboBox cmb)
                    {
                        cmb.BackColor = Color.FromArgb(55, 65, 81);
                        cmb.ForeColor = Color.White;
                    }
                    else if (control is DataGridView dgv)
                    {
                        dgv.BackgroundColor = Color.FromArgb(31, 41, 55);
                        dgv.GridColor = Color.FromArgb(55, 65, 81);
                        dgv.DefaultCellStyle.BackColor = Color.FromArgb(31, 41, 55);
                        dgv.DefaultCellStyle.ForeColor = Color.White;
                        dgv.DefaultCellStyle.SelectionBackColor = Color.FromArgb(59, 130, 246);
                        dgv.DefaultCellStyle.SelectionForeColor = Color.White;
                        dgv.ColumnHeadersDefaultCellStyle.BackColor = Color.FromArgb(17, 24, 39);
                        dgv.ColumnHeadersDefaultCellStyle.ForeColor = Color.White;
                        dgv.EnableHeadersVisualStyles = false;
                    }
                    else if (control is Label lbl)
                    {
                        if (lbl.ForeColor == Color.FromArgb(51, 65, 85) || lbl.ForeColor == Color.FromArgb(17, 24, 39) || lbl.ForeColor == Color.Black || lbl.ForeColor == SystemColors.ControlText)
                        {
                            lbl.ForeColor = Color.FromArgb(209, 213, 219); 
                        }
                    }
                    else if (control is GroupBox grp)
                    {
                        grp.ForeColor = Color.White;
                    }
                }
                else
                {
                    
                    if (control is Panel panel)
                    {
                        if (panel.BackColor == Color.FromArgb(31, 41, 55))
                        {
                            panel.BackColor = Color.White;
                            panel.ForeColor = Color.FromArgb(17, 24, 39);
                        }
                    }
                    else if (control is Button btn)
                    {
                        if (btn.BackColor == Color.FromArgb(55, 65, 81))
                        {
                            btn.BackColor = Color.White;
                            btn.ForeColor = Color.FromArgb(75, 85, 99);
                            btn.FlatAppearance.BorderColor = Color.FromArgb(209, 213, 219);
                        }
                    }
                    else if (control is TextBox txt)
                    {
                        if (txt.BackColor == Color.FromArgb(55, 65, 81))
                        {
                            txt.BackColor = Color.White;
                            txt.ForeColor = Color.Black;
                        }
                    }
                    else if (control is ComboBox cmb)
                    {
                        if (cmb.BackColor == Color.FromArgb(55, 65, 81))
                        {
                            cmb.BackColor = Color.White;
                            cmb.ForeColor = Color.Black;
                        }
                    }
                    else if (control is DataGridView dgv)
                    {
                        if (dgv.BackgroundColor == Color.FromArgb(31, 41, 55))
                        {
                            dgv.BackgroundColor = Color.White;
                            dgv.GridColor = Color.LightGray;
                            dgv.DefaultCellStyle.BackColor = Color.White;
                            dgv.DefaultCellStyle.ForeColor = Color.Black;
                            dgv.DefaultCellStyle.SelectionBackColor = SystemColors.Highlight;
                            dgv.DefaultCellStyle.SelectionForeColor = SystemColors.HighlightText;
                        }
                    }
                    else if (control is Label lbl)
                    {
                        if (lbl.ForeColor == Color.FromArgb(209, 213, 219))
                        {
                            lbl.ForeColor = Color.FromArgb(51, 65, 85);
                        }
                    }
                    else if (control is GroupBox grp)
                    {
                        grp.ForeColor = Color.Black;
                    }
                }

                if (control.Controls != null && control.Controls.Count > 0)
                {
                    ApplyControlTheme(control.Controls);
                }
            }
        }
    }
}