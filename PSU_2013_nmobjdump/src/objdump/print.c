/*
** print.c for  in /why/are/you/watching/my/path
** 
** Made by Maxime renaud
** Login   <root@epitech.net>
** 
** Started on  Tue Mar 11 21:15:53 2014 Maxime renaud
** Last update Sun Mar 16 20:24:18 2014 Maxime renaud
*/

# include		<stdio.h>
# include		"../../include/my_elf.h"

static	void		hex_format(int ecx, int size, char *buff,
				   unsigned int addr)
{
  int			esi;

  esi = -1;
  printf(" %04x", addr);
  while (++esi < 16 && ecx + esi < size)
    {
      if (!(esi % 4))
	printf(" ");
      printf("%02x", buff[ecx + esi] & 0x0000000ff);
    }
  if (esi < 16)
    while (esi < 16)
      {
	if (!(esi % 4))
	  printf(" ");
	printf("  ");
	esi++;
      }
  printf("  ");
  return ;
}

static	void		str_format(int ecx, int size, char *buff)
{
  int			esi;

  esi = -1;
  while (++esi < 16 && ecx + esi < size)
    {
      if (buff[ecx + esi] >= ' ' && buff[ecx + esi] <= '~')
	printf("%c", buff[ecx + esi]);
      else
	printf(".");
    }
  if (esi < 16)
    while (esi++ < 16)
      printf(" ");
  printf("\n");
  return ;
}

extern	void		print_inside(Elf32_Shdr *sect, t_elf *elf,
				     Elf64_Shdr *sect64, unsigned int addr)
{
  unsigned int		x;

  x = 0;
  if (elf->bit == true)
    while (x < sect->sh_size)
      {
	hex_format(x, sect->sh_size, (char*)(elf->maddr + sect->sh_offset),
		   addr);
	str_format(x, sect->sh_size, (char*)(elf->maddr + sect->sh_offset));
	addr += 16;
	x += 16;
      }
  else
    while (x < sect64->sh_size)
      {
	hex_format(x, sect64->sh_size, (char*)(elf->maddr + sect64->sh_offset)
		   , addr);
	str_format(x, sect64->sh_size,
		   (char*)(elf->maddr + sect64->sh_offset));
	addr += 16;
	x += 16;
      }
  return ;
}
