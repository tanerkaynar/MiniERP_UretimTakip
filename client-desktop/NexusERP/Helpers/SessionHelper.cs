using System;

namespace NexusERP.Helpers
{
    public static class SessionHelper
    {
        public static string KullaniciAdi { get; set; }
        public static string Rol { get; set; }
        public static int? PersonelID { get; set; }
        public static string PersonelAdSoyad { get; set; }

        public static void Clear()
        {
            KullaniciAdi = null;
            Rol = null;
            PersonelID = null;
            PersonelAdSoyad = null;
        }
    }
}