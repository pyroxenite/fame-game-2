from PIL import Image
from PIL import ImageOps
import os

# %%
folders = [ folder for folder in os.listdir() if os.path.isdir(folder) ]

for folder in folders:
    for imName in os.listdir(folder):
        if imName[-3:] == "png":
            print(imName)


# %%

def flipImage(folder, file):
    im = Image.open(folder + "/" + file)
    im = ImageOps.mirror(im)
    im.save(folder + "/" + file)
# %%
