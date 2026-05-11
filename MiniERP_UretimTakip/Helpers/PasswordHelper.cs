using System.Security.Cryptography;
using System.Text;

namespace MiniERP_UretimTakip.Helpers
{
    public static class PasswordHelper
    {
        public static string Sha256Hash(string text)
        {
            using (var sha256 = SHA256.Create())
            {
                byte[] bytes = Encoding.UTF8.GetBytes(text);
                byte[] hashBytes = sha256.ComputeHash(bytes);
                var builder = new StringBuilder();

                foreach (byte b in hashBytes)
                    builder.Append(b.ToString("x2"));

                return builder.ToString();
            }
        }
    }
}
