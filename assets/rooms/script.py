from PIL import Image
import argparse

def resize_image(input_path, output_path, new_width, new_height):
    # Ouvrir l'image
    img = Image.open(input_path)
    
    # Redimensionner sans anti-aliasing (utiliser Image.NEAREST pour garder les pixels nets)
    img_resized = img.resize((new_width, new_height), Image.NEAREST)
    
    # Sauvegarder l'image redimensionnée
    img_resized.save(output_path)
    print(f"Image redimensionnée enregistrée sous {output_path}")

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Redimensionne une image en conservant le pixel art.")
    parser.add_argument("input", help="Chemin du fichier d'entrée")
    parser.add_argument("output", help="Chemin du fichier de sortie")
    parser.add_argument("width", type=int, help="Nouvelle largeur de l'image")
    parser.add_argument("height", type=int, help="Nouvelle hauteur de l'image")
    
    args = parser.parse_args()
    resize_image(args.input, args.output, args.width, args.height)
