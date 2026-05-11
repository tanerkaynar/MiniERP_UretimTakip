using System;

namespace MiniERP_UretimTakip.Models
{
    public class UretimKaydi
    {
        public int UretimID { get; set; }
        public int UrunID { get; set; }
        public int MakineID { get; set; }
        public int PersonelID { get; set; }
        public int UretimAdedi { get; set; }
        public DateTime UretimTarihi { get; set; }
        public string Aciklama { get; set; }
    }
}
